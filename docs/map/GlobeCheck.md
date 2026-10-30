# GlobeCheck.java - 3,391 lines · 108 methods · 78 constants · harnesses

`ham/citybuildersim/GlobeCheck.java` - generated 2026-10-30 by CodeMap; line numbers are as of that run.

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
>      pixels the square holds within handoverKm() of the city (MS3: farther,
>      and off the square, the charts draw both sides of its edge) - and the
>      legend is the plane's; over views straddling the reach on the fixture's three
>      windows and, named, Cape Breton north of Baddeck, at his scale, 2, 4 and
>      8 plots a pixel and the closest, the classes the nodes give are the
>      plane's own plots' (exactly at one plot a pixel, within NEAR_PAST_FOLDED
>      and NEAR_PAST_CLASSES at 2 to 8) on both sides of the reach, no more
>      dithered than the plane's own (the far rule's speckle printed beside
>      them), and a small lake is the plane's frayed lake, not the far rule's
>      disc; a frame of the camera over the nodes held within FRAME_MS.
>  2d. THE SLANTED GROUND, MEASURED (MS3, Jerus 2026-10-26: over Goose Bay from
>      a Singapore city, at the 2 km scale, every patch of cover is a thin
>      diagonal streak): streak() reads a ground's elongation and direction
>      from its change rates along four offsets, and response() what it reads
> ... (119 more lines in the source)

**Uses:** [GlobeFrame](GlobeFrame.md) (266), [NationsMap](NationsMap.md) (155), [World](World.md) (142), [TradeLanes](TradeLanes.md) (91), [GlobePicture](GlobePicture.md) (52), [EarthData](EarthData.md) (45), [MapFrame](MapFrame.md) (38), [MapTiles](MapTiles.md) (31), [EarthTerrainCheck](EarthTerrainCheck.md) (30), [EarthTerrain](EarthTerrain.md) (28), [MapCheck](MapCheck.md) (28), [EarthProj](EarthProj.md) (26), [WorldData](WorldData.md) (25), [Nations](Nations.md) (21), [Timing](Timing.md) (10), [TileRaster](TileRaster.md) (8), [EarthCheck](EarthCheck.md) (5), [WorldDataCheck](WorldDataCheck.md) (5)

**Used by (1):** [EarthTerrainCheck](EarthTerrainCheck.md)

## Sections

| line | section |
|---:|---|
| 305 | 1. THE ORTHOGRAPHIC PROJECTION |
| 357 | 2. THE VIEW |
| 529 | 2b. THE NEAR VIEW ANYWHERE AND NO WALL (MS1) |
| 885 | 2c. THE NEAR VIEW PAST THE REACH IS THE PLANE'S GROUND (MS2) |
| 1252 | 2d. THE SLANTED GROUND, MEASURED, AND FIXED BY THE CHARTS (MS3) |
| 1895 | 3. THE HANDOFF |
| 2012 | 4. THE PLANE'S CAMERA IS TODAY'S FAR NODES |
| 2063 | 5. THE GLOBE'S CAMERAS |
| 2132 | 6. THE PLANET'S PICTURE |
| 2280 | 7. A FRAME |
| 2313 | 8. THE RELIEF IN THE PICTURE (ET1 slice D; spec-earth2-terrain 9.3) |
| 2451 | 9 TO 12. THE NATIONS ON THE MAP (WN2, slice A; docs/studies/spec-nations.md 6.1 and 8, board 1) |
| 2542 | · 9. the levels |
| 2630 | · 9. culled to the view, at its level, timed |
| 2676 | · 10. the dots and labels |
| 2759 | · 11. the hit test |
| 2836 | · 12. the card |
| 2941 | 13 AND 14. THE CITY'S TRADE ON THE MAP (WN3, slice C; docs/studies/spec-nations.md 8, board 1, and 6.4) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 204 | `GlobeCheck.POINTER_PX` | `1e-3` | A zoom keeps the point under the pointer under it to within this, in pixels: a thousandth (★E8) - the solver's rounding, nothing a player sees. |
| 207 | `GlobeCheck.ROUND_TRIP_M` | `1e-6` | The orthographic round trip holds a point to within this, in metres: a micrometre (★E8) - at the limb the inverse loses digits, so the planet's points are held within LIMB_DEG of the centre. |
| 210 | `GlobeCheck.LIMB_DEG` | `89` | ...a point on the planet within this many degrees of the view's centre: 89, the limb's last degree left out, where the disc squeezes 111 km of ground into 17 m. |
| 213 | `GlobeCheck.ROUNDS` | `100_000` | Random views and points the projection is tried on: 100,000. |
| 216 | `GlobeCheck.REACH_PARTING_PX` | `1.25` | At REACH_KM with the roll eased, the plane and the globe part by under this, in pixels: 1.25 (★E8) - the study's 1.16 px, measured with the globe scaled to the plane's mean scale there (globe-prototype.py), 1.21 as th... |
| 219 | `GlobeCheck.BEARINGS` | `{ 0, 45, 90, 135, 180 }` | The bearings a view away from the city is looked at, degrees: the study's five. |
| 222 | `GlobeCheck.TABLE_KM` | `{ 100, 300, 500, 1000 }` | The distances the study's table measured the swap at, km (spec-globe 3.3). |
| 225 | `GlobeCheck.CITIES` | `{ { 44.65, - 63.57 }, { 14.60, 120.98 } }` | The study's cities, its boards' (spec-globe 1): Halifax and Manila. |
| 228 | `GlobeCheck.FOUNDING_W` | `560, FOUNDING_H = 270` | The founding globe's canvas, pixels: 560 x 270 (FoundingScreen's GLOBE_W and GLOBE_H; spec-globe 3.2's second column). |
| 231 | `GlobeCheck.TURN_PX` | `0.1` | A turn of the globe of this many pixels flips the forest on no more than this share of the dry pixels: a tenth (★E8) - the dither's cells are at least a pixel each way, so a point moving a tenth of a pixel crosses a c... |
| 234 | `GlobeCheck.FRAME_MS` | `100` | A frame of the expanded map from the planet's picture in no more than this of the main thread's CPU, ms: 100 (spec-globe 3.6 measured 96 to 102 ms on one cloud thread, 50 on two, and called that live). |
| 237 | `GlobeCheck.FRAME_ROUNDS` | `10` | Frames timed, the least taken, after as many to warm. |
| 245 | `GlobeCheck.FIXTURE_PRINTS` | `Map.of("Rotterdam", 0xed2ff708L, "Perth", 0x0b1d7f68L, "Paris", 0xc3979824L, ...` | The CRC32 of every stride's far node over the fixture's four windows and the world's (fingerprint()), as 0.7.131 drew them before the camera (probe E8Fingerprint on its build, 19f1cad), but for what the earth-2 fixtur... |
| 248 | `GlobeCheck.FULL_SPOTS` | `{ { 51.90625, 4.48125 }, { 44.65, - 63.57 }, { 14.60, 120.98 }, { 72, - 40 },...` | The full file's spots: Rotterdam, Halifax, Manila, Greenland's ice, McMurdo past EarthProj.BLEND_MAX_LAT, Chukotka on the 180th meridian, Finland's lakes and the Sahara. |
| 255 | `GlobeCheck.FULL_PRINTS` | `{ 0xf291ea65L, 0x208e9728L, 0xd85a163cL, 0x734fdb04L, 0xf5f461adL, 0xbdc3b5b7...` | ...and their nodes' CRC32, as 0.7.131 drew them but for the shallow sea (ET1, 2026-10-24: World.SHALLOW is now a class the far nodes hold, so the figures moved). |
| 259 | `GlobeCheck.out` | `System.out` | Where the harness prints: the console. |
| 267 | `GlobeCheck.R` | `EarthProj.RADIUS_M` | The planet's radius in metres (EarthProj.RADIUS_M), which the sections measure arcs and the disc by. |
| 534 | `GlobeCheck.NEAR_KM` | `GlobeFrame.HANDOFF_KM / 4` | A near scale, km a pixel: a quarter of the handoff's, four plots a pixel (MS1's views closer than the handoff, on the plane and past its reach). |
| 537 | `GlobeCheck.STEP_PX` | `150` | The pixels a drag outward takes at a time in section 2b: 150 - at NEAR_KM, 18 km, so the reach is crossed in about seventeen. |
| 540 | `GlobeCheck.STEPS_MOST` | `100` | The most drags outward section 2b takes: 100, far past twice the reach at NEAR_KM. |
| 543 | `GlobeCheck.FAR_SPOTS` | `{ { 14.60, 120.98 }, { - 31.95, 115.86 }, { - 17.0, 179.9 }, { - 17.0, - 179....` | The spots section 2b zooms in on far from either city, {lat, lon}: Manila, Perth, and a spot each side of the antimeridian (the date line) at 17 degrees south and Manila's latitude. |
| 552 | `GlobeCheck.CROSSING_HANDOFF_SHARE` | `0.01` | The share of a patch's pixels whose class the plane and the GROUND tier's far rule may draw differently at the handoff, over a patch at the reach's edge: 1 percent (measured 0.15 to 0.65 percent over six patches - the... |
| 558 | `GlobeCheck.CROSSING_NEAR_SHARE` | `0.06` | ...and at NEAR_KM, where the plane draws a far node of four plots a pixel and the camera the far rule at its least stride, DITHER_STRIDE (16 plots), so a coast or a river parts by up to a pixel and four: 6 percent (me... |
| 890 | `GlobeCheck.JERUS_KM` | `2.0 / 120` | Jerus's near scale, km a pixel: 2 km in the scale bar's 120 px (ui/MapView.SCALE_BAR_PX), 1.8 px a plot - the view of his two screenshots. |
| 897 | `GlobeCheck.NEAR_PAST_SAME` | `1 - 1e-5` | At one plot a pixel (his scale and the closest) the classes the plane's nodes give through the camera are the plane's own, plot for plot: the share of a view's pixels, either side of the reach, whose class is the clas... |
| 905 | `GlobeCheck.NEAR_PAST_FOLDED` | `0.98` | At 2, 4 and 8 plots a pixel - where the plane draws a far node a pixel of which is one sample of its 2, 4 or 8 plots, and the camera reads that node - the share of pixels whose class is the class of the plot under the... |
| 908 | `GlobeCheck.NEAR_PAST_CLASSES` | `0.90` | ...and with the forest and the grass told apart: 90 percent (measured 91.6 to 98.3% on the fixture, 96.7 to 97.6% on the full file). |
| 911 | `GlobeCheck.NEAR_PAST_SPECKLE` | `0.001` | The share of pixels whose class differs from both horizontal neighbours the reprojected nodes may draw over the plane's own: a tenth of a percent - the camera reads the same node pixels the plane does, turned by a few... |
| 914 | `GlobeCheck.FAR_SPECKLE_AT_LEAST` | `0.05` | The far rule's speckle on a forested window the fixture must cause, for the comparison to mean anything: 5 percent of the pixels (measured 15.2% over Rotterdam and 11.9% over Paris at Jerus's scale; the plane's 0.04%). |
| 917 | `GlobeCheck.LAKE_IS_A_DISC` | `0.02` | A small lake the far rule draws as a disc is one whose pixels part from the equal-area disc about their centroid by no more than this share of their number: 2 percent (the far rule's discs measured 0.000 at 1.6 to 7.4... |
| 920 | `GlobeCheck.LAKE_IS_FRAYED` | `0.10` | ...and the plane's frayed one parts by at least this: 10 percent (its fray is BLOB_FRAY of its own noise: measured 0.10 to 0.23 over the lakes picked). |
| 923 | `GlobeCheck.LAKE_SAME_IOU` | `1 - 1e-3` | The reprojected lake is the plane's: their pixels' intersection over their union at one plot a pixel, within a thousandth of 1 (the share of section 2c's first figure). |
| 926 | `GlobeCheck.LAKE_DISC_IOU` | `0.9` | ...and the far rule's disc is not the plane's lake: at most this (measured 0.55 to 0.85 over the lakes picked). |
| 929 | `GlobeCheck.LAKE_LEAST_PLOTS` | `12` | Small lakes at least this many plots across the radius are tried (smaller are a few pixels at his scale), the least of them picked in each source: 12 plots (0.4 km2), four of them. |
| 930 | `GlobeCheck.LAKES_PICKED` | `4` |  |
| 933 | `GlobeCheck.LAKE_VIEW_PX` | `256` | A lake view's side, pixels: 256, the lake's radius a quarter of it. |
| 936 | `GlobeCheck.NEAR_SCALES` | `{ JERUS_KM, NEAR_KM / 2, NEAR_KM, NEAR_KM * 2, GlobeFrame.leastKmPx() }` | The scales the views straddling the reach are drawn at, km a pixel: Jerus's (1.8 px a plot), then 2, 4 and 8 plots a pixel - NEAR_KM among them - and the closest, 16 px a plot. |
| 946 | `GlobeCheck.NO_NODE` | `- 1` | What the camera draws of the plane's nodes (GlobeFrame.ground()) over a w x h view, a class a pixel (MapTiles.nodeTerrain()), NO_NODE where it drew none, OFF where it drew the far rule's place (beyond()) - and how man... |
| 1257 | `GlobeCheck.STREAK_LAG_PX` | `4` | The offset the ground's change rates are read at, in pixels: 4 - short enough that the share of pixel pairs that differ grows with the offset's length across a patch of the 2 km view's 20 to 60 px. |
| 1260 | `GlobeCheck.STREAK_VIEW_PX` | `512` | The side of the views the ground's streak is read over, in pixels: 512 (the fixture's windows hold the 2c view of 1,345 x 806 at his scale, and this is a fraction of it). |
| 1263 | `GlobeCheck.STREAK_KM` | `{ GlobeFrame.REACH_KM *(1 + 1e-6), 2000, 4000, 6000, 8000, 10000, 12000 }` | The distances the city is placed from the spot, in km: just past the reach (the spot's reference, where Lambert's plane is true to a thousandth) and 2,000 to 12,000 km, where it squashes the ground by 1.03 to 2.9 to 1. |
| 1266 | `GlobeCheck.STREAK_READ_TOL` | `0.10` | The measure reads a field of no stretch as 1 within this, and a stretched one at most this above its stretch: 0.10 - set before the first run. |
| 1269 | `GlobeCheck.STREAK_READ_FLOOR` | `0.65` | ...and a stretched field at least this share of its stretch: 0.65 - the measure's own reading, set from the calibration and not to pass a case: it reads 4 to 1 at 0.683 of it on the equal-area fields (lag saturation a... |
| 1272 | `GlobeCheck.STREAK_PHI_DEG` | `5` | ...and the field's direction within this many degrees of the stretch's, for a stretch of at least 2: 5. |
| 1275 | `GlobeCheck.STREAK_SEEDS` | `8` | The synthetic fields a stretch's response is averaged over, and the fields of the plane's noise a placement's: 8 (one field's E varies by a tenth). |
| 1278 | `GlobeCheck.STREAK_TRACKS` | `0.15` | The plane's noise under the pixels is stretched as the measure reads Lambert's projection stretch it: its E over its E at the reach is within this share of response(sigma) / response(1), at every placement; and the dr... |
| 1281 | `GlobeCheck.STREAK_BEARING_DEG` | `10` | Where the distortion is at least STREAK_FROM_SIGMA the drawn ground's streak runs along the line from the spot to the city to within this many degrees: 10. |
| 1284 | `GlobeCheck.STREAK_FROM_SIGMA` | `1.5` | ...the shape distortion from which the streak's direction and its rise are asserted: 1.5 (the patches half again as long one way as the other). |
| 1287 | `GlobeCheck.STREAK_SHOWN` | `1.5` | A ground read at this E / E_ref or more is visibly streaked: 1.5 - the measure reads Jerus's screenshot, Goose Bay from Singapore, at 2.4, and a window at 12,000 km at 1.6 to 2.0. |
| 1290 | `GlobeCheck.STREAK_STRETCHES` | `{ 1, 2, 4 }` | The stretches the measure is calibrated on: 1, 2 and 4 to 1. |
| 1293 | `GlobeCheck.STREAK_TURNED_S` | `3, STREAK_TURN_DEG = 30` | ...and once a stretch of STREAK_TURNED_S to 1 turned STREAK_TURN_DEG degrees on the screen: 3 to 1, 30 degrees. |
| 1296 | `GlobeCheck.STREAK_SEED` | `0x5EED_2DL` | The synthetic fields' noise seed. |
| 1299 | `GlobeCheck.SINGAPORE` | `{ 1.2903, 103.8519 }, HAPPY_VALLEY = { 53.3017, - 60.3261 }` | Singapore (Jerus's city, the brief's) and Happy Valley-Goose Bay, Labrador, whose place Goose Bay (GOOSE_BAY_NORTH_KM due north of it) is. |
| 1302 | `GlobeCheck.GOOSE_BAY_NORTH_KM` | `42` | ...the distance due north of Happy Valley-Goose Bay Jerus's hover named, in km: 42. |
| 1305 | `GlobeCheck.TROMSO` | `{ 69.65, 18.96 }` | A city where the plane's up is some degrees from north a little way out, for the roll's sign: Tromso (the view is put the reach's edge due east of it). |
| 1390 | `GlobeCheck.RESPONSES` | `new java.util.HashMap<>()` | What response() has read, by stretch and turn. |
| 1421 | `GlobeCheck.SPHERE_SEEDS` | `4` | The synthetic fields of the sphere's noise a placement's E is averaged over: 4 of the STREAK_SEEDS (its noise costs twice the plane's a pixel). |
| 1618 | `GlobeCheck.SEAM_TOL` | `0.15` | The change rate of pixel pairs across the hand-over's line (or the band's far edge), over the rate on one side, may exceed the same figure of the same pixels drawn by the chart alone by this share: 0.15 - the most the... |
| 1621 | `GlobeCheck.SHARE_TOL` | `0.05` | The class shares of the ground either side of the hand-over's line (water, forest, grass, the rest), pooled over the views, differ by at most this: 0.05. |
| 1624 | `GlobeCheck.SEAM_BEARINGS` | `{ 0, 90, 180, 270 }` | The bearings the city is put at from the view's centre, degrees, to pool a seam over: the four screen directions - a pixel is on the plane's side in half of them and the chart's in the other, so the ground each side h... |
| 1738 | `GlobeCheck.SEAM_NORTH_KM` | `60` | How far north of Jerus's hover (GOOSE_BAY_NORTH_KM from Happy Valley) the seam views near Goose Bay are centred, km: 60 - his own spot lies at the edge of Hamilton Inlet's 40 km wide "river" run of the data, which bot... |
| 1741 | `GlobeCheck.SEAM_SHIFTS` | `{ - 0.5, 0.5 }` | The shifts along the line the views are taken at, as shares of the view's width: either side of the centre, so the ground the views hold is more than the one window's centre. |
| 1744 | `GlobeCheck.SEAM_SHOWN` | `1.5` | A seam made on purpose (the sphere's noise switched on at the hand-over with no band) reads at least this: 1.5 - so the measure can see one. |
| 1782 | `GlobeCheck.SPREAD_POINTS` | `1<<16` | The points the sphere's noise spread is read over: 2^16. |
| 1785 | `GlobeCheck.SPREAD_TOL` | `0.02` | The sphere's noise's standard deviation is within this share of the plane's: 0.02. |
| 1788 | `GlobeCheck.RANK_TOL` | `0.01` | ...and the share of its points under the ranking table's value for p (EarthTerrain.below()) is within this of p, and of the plane's: 0.01. |
| 1791 | `GlobeCheck.RANK_P` | `{ 0.05, 0.25, 0.5, 0.75, 0.95 }` | The ranks the shares are read at. |
| 2318 | `GlobeCheck.RELIEF_SIGMAS` | `4, RELIEF_ROUNDING = 0.03` | The picture's relief shares are the data's within this many binomial spreads of the dry texels drawn (a texel is a hash's draw against the 4' block's share, as a far node's pixel is) and a rounding of the slope layer'... |
| 2456 | `GlobeCheck.VERTICES_MOST` | `45_000` | The most vertices the whole planet's borders may draw: 45,000 - measured 14,814 on the 1,389 x 868 view facing Halifax at the 4 km level, 2026-10-28, against 56,914 at the file's own; three times the measure, rounded up. |
| 2459 | `GlobeCheck.NAME_PX` | `6.5, FIGURES_PX = 5.7` | The widths the harness gives the labels, pixels a character: the board's 11 px sans name (6.5) and its 9.5 px mono figures (5.7) - the screen measures its own fonts. |
| 2460 | `GlobeCheck.NAME_W` | `s -> s.length() * NAME_PX, FIGURES_W = s -> s.length() * FIGURES_PX` |  |
| 2463 | `GlobeCheck.YEAR` | `2020` | The year the nations' figures are read for: 2020, a published one (the marks are read at 2000, 2025 and 2030 too). |
| 2466 | `GlobeCheck.D_PER_USD` | `1.16` | The game dollar's price in US$ the cards are given: D$1.16 a US$ (a figure to carry through, not a dial). |
| 2469 | `GlobeCheck.VIEWS` | `{ { 0, 0, 0 }, { 50, - 30, 10 }, { 50, 10, 3 }, { CITIES [ 0 ] [ 0 ], CITIES ...` | The views of 9 and 10, {lat, lon, km a pixel} (0: the whole planet facing the city), and their names: the planet, the North Atlantic, Europe and the GROUND tier at the city. |
| 2470 | `GlobeCheck.VIEW_NAMES` | `{ "the whole planet", "the North Atlantic at 10 km a pixel", "Europe at 3 km ...` |  |
| 2840 | `GlobeCheck.RANK` | `Pattern.compile("(\\d+)(st\|nd\|rd\|th) of(\\d+)")` |  |
| 2946 | `GlobeCheck.EUROPE` | `{ { 54.0, - 2.0 }, { 53.4, - 8.0 }, { 64.9, - 18.6 }, { 60.5, 8.5 }, { 46.6, ...` | Twelve partners of a Halifax city, {lat, lon}: the ten nodes of Europe that are partners at 1,000 sold and 500 bought, Poland at 100 and 100 (under MIN_LANE) and Sweden at 200 and 100 (at it). |
| 2950 | `GlobeCheck.TRADE_ROUNDS` | `31, TRADE_WARM = 300` | The rounds a trade timing takes the median of (31), after as many as it takes to warm the JIT (300). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 257 | `static int fails` |  |
| 1086 | `double same` | The least share of pixels at one plot a pixel whose class is the plot's; at 2 to 8 plots a pixel the least folded, and the least told apart; the largest the camera's speckle goes over the plane's; the far rule's most ... |
| 1087 | `final List<String[]> lines` |  |
| 1634 | `final long[][] pairs` | Pairs at the offset that cross the line [0] and that lie on one side [1], and how many of them change class: [0] the drawn ground, [1] the twin. |
| 1636 | `final long[][] shares` | Pixels each side by group, [side][group]: side 0 nearer the city. |
| 2181 | `int mixed, past` |  |
| 2182 | `double mean, worst, pictureLand, dataLand` |  |
| 2183 | `double[] diffs` |  |
| 2234 | `long texels, seaWrong, iceWrong, desertWrong, dry, forest` |  |
| 2235 | `double forestData` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 201 | 3191 | **type** `public class GlobeCheck` | The Earth's map as a globe, its model half (0.7.132, batch E8; docs/studies/spec-globe.md 3 and 4's E8 row): EarthProj's orthographic projection, GlobeFrame's view and its handoff to the city's plane, the far rule's t... |
| 261 | 4 | `static void check(String label, boolean ok)` |  |
| 269 | 35 | `public static void main(String[] args) throws Exception` |  |

### 1. THE ORTHOGRAPHIC PROJECTION (lines 305-356)

| line | len | member | says |
|---:|---:|---|---|
| 309 | 47 | `static void ortho()` |  |

### 2. THE VIEW (lines 357-528)

| line | len | member | says |
|---:|---:|---|---|
| 361 | 8 | `static double[] pointer(Random r, GlobeFrame f)` |  |
| 371 | 4 | `static double offPx(GlobeFrame f, double[] ll, double sx, double sy)` | How far, in the view's pixels, (lat, lon) lies from screen point (sx, sy): null where it is not seen. |
| 377 | 6 | `static boolean northUpCan(double lat, double sx, double sy, double w, double h, double kmPx)` | Whether north up can put (lat) at screen point (sx, sy) of a wide view at kmPx: the spherical triangle of the pole, the centre and the point allows it - \|sin lat\| no more than the root of cos^2 c + sin^2 c cos^2 a, c ... |
| 384 | 130 | `static void view()` |  |
| 516 | 3 | `static double[] atReach(double[] city, double bearing)` | The point at the reach's edge from a city along a bearing: REACH_KM out, a hair inside it as a pan's stop leaves the view. |
| 521 | 7 | `static double numericRoll(EarthProj p, double lat, double lon)` | The plane's up at (lat, lon) against north, degrees clockwise, by a small step north through EarthProj.forward(). |

### 2b. THE NEAR VIEW ANYWHERE AND NO WALL (MS1) (lines 529-884)

| line | len | member | says |
|---:|---:|---|---|
| 561 | 6 | `static byte planeClass(World.Key key, long plots, double x, double y, Map<Long, byte[]> nodes)` | The class the city's plane draws at plot (x, y), at `plots` plots a pixel: the far node holding it (MapTiles.nodeTerrain()), its pixel under the plot. |
| 572 | 17 | `static double crossingShare(World.Key key, double cityLat, double cityLon, double lat, double lon, double kmPx, int n)` | The share of a n x n view's pixels whose class the city's plane draws differently from the GROUND tier's far rule through the globe's camera, the forest folded into the dry ground it stands on (its dither is a hash): ... |
| 591 | 5 | `static double mixed(byte[] piece, int w, int col, int h)` | How the share of land and of water among a column's pixels mixes: the lesser of the two, 0 for a column all one or the other. |
| 597 | 287 | `static void nearAnywhere(Map<String, EarthTerrainCheck.Spot> spots, String id, EarthData full)` |  |

### 2c. THE NEAR VIEW PAST THE REACH IS THE PLANE'S GROUND (MS2) (lines 885-1251)

| line | len | member | says |
|---:|---:|---|---|
| 939 | 5 | `static int[] widen(byte[] classes)` | A class as an int in the nodes ground() reads: the node's classes widened, so the camera's pixels are classes. |
| 947 | 3 | `static int[] cameraClasses(World.Key key, GlobeFrame f, int w, int h, int[] missing)` |  |
| 952 | 6 | `static int[] cameraClasses(World.Key key, GlobeFrame f, int w, int h, int[] missing, boolean charts)` | ...and, where `charts`, the view's chart nodes past the hand-over too (MS3: MapTiles.chartTerrain() at the view's chart and plots), so every pixel on the disc is drawn but for the nodes missing. |
| 960 | 9 | `static int[] cameraClassesWith(World.Key key, GlobeFrame f, int w, int h, int[] missing, GlobeFrame.NodePixels charts)` | ...with the chart nodes made by `charts` (a node's classes widened; each made once), or none where it is null. |
| 971 | 8 | `static int[] plotClasses(World world, GlobeFrame f, int w, int h)` | The class of the plot under each pixel, as the plane's own tiles hold it (World.terrainAt(), stride one) - -2 off the planet. |
| 981 | 11 | `static int[] planeDraws(World.Key key, GlobeFrame f, int w, int h)` | What the plane draws over the same view at the same scale, its own frame unturned: each pixel the class of its far node's pixel at `plots` plots (planeClass()). |
| 994 | 3 | `static int[] farRuleDraws(World.Key key, GlobeFrame f, int w, int h)` | The far rule's classes through the camera over the same view (GlobeFrame.groundPiece(): what the GROUND tier drew past the reach before MS2). |
| 999 | 12 | `static double speckle(int[] a, int w, int h)` | The share of drawn pixels (a class, not NO_NODE or off the planet) whose class differs from both horizontal neighbours: the speckle of a dithered ground. |
| 1013 | 10 | `static double agree(int[] a, int[] b, boolean fold, GlobeFrame f, int w, Boolean inside)` | The share of pixels drawn in both (a class each) whose classes agree, the forest and the desert folded into the grass if `fold`; `inside` restricts them to those within the reach (true), past it (false), or all (null). |
| 1025 | 12 | `static double[] discParting(int[] a, int w, int h)` | A lake's pixels - fresh water in `a` - their count, and how far they part from the equal-area disc about their centroid, as a share of their count: 0 for a disc, 2 at the most. |
| 1039 | 9 | `static double freshIoU(int[] a, int[] b)` | Two views' fresh water, intersection over union. |
| 1050 | 1 | **type** `record LakePick(String where, double lat, double lon, double plots, double[] plane, double[] camera, double...` | One small lake picked: its place, its radius in plots, and what each rule drew of it. |
| 1058 | 24 | `static List<LakePick> lakes(String where, EarthData data, World.Key key, double cityLat, double cityLon, double lat, double lon...` | The small lakes of the 1-degree tiles round (lat, lon) within `withinKm` of it, at least LAKE_LEAST_PLOTS across: each viewed through the camera at the scale that makes its radius a quarter of LAKE_VIEW_PX, the plane'... |
| 1084 | 28 | **type** `static final class Near` | What the views straddling the reach drew, the worst of each figure (the lines print them). |
| 1090 | 21 | `void view(String name, World.Key key, double[] city, double[] at, double km, int n, int m)` _(in GlobeCheck.Near)_ | The view centred at `at` at `km`, the city at `city` on the world `key`: its classes through the camera against the plane's own plots, the plane's own picture and the far rule's. |
| 1113 | 138 | `static void nearPastReach(Map<String, EarthTerrainCheck.Spot> spots, String id, EarthData data, EarthData full)` |  |

### 2d. THE SLANTED GROUND, MEASURED, AND FIXED BY THE CHARTS (MS3) (lines 1252-1894)

| line | len | member | says |
|---:|---:|---|---|
| 1308 | 1 | `static double fold180(double deg)` | An angle folded into [0, 180). |
| 1311 | 4 | `static double apart180(double a, double b)` | The angle between two directions given as angles folded into [0, 180), 0 to 90. |
| 1324 | 23 | `static double[] streak(int[] cls, int w, int h, int lag)` | The ground's elongation from its change rates: for four offsets A = (lag, 0), B = (0, lag), C = (k, k), D = (k, -k), k = round(lag / sqrt 2) (screen x right, y down) the share p of pixel pairs - both drawn, class >= 0... |
| 1349 | 1 | `static double streakBearing(double phi, double roll)` | The streak's direction on the planet, degrees clockwise from north folded into [0, 180): a screen direction beta clockwise from up has the bearing beta - roll (GlobeFrame.roll() is the angle clockwise from the view's ... |
| 1352 | 1 | `static double planeDistortion(double km)` | Snyder's shape distortion of Lambert's azimuthal equal-area projection at `km` along the great circle from its centre, k / h = (1 / cos(c / 2)) / cos(c / 2) = 2 / (1 + cos c) - an independent textbook formula, as 1's ... |
| 1355 | 5 | `static long[] fieldSeeds(int field)` | The octave streams of one synthetic field's noise, EarthTerrain.noiseAt()'s `os`: the harness's own seed, the field's number and the octave (NOISE_LO to NOISE_HI, as the rule's). |
| 1362 | 11 | `static int[] belowMedian(double[] values, boolean[] used)` | The class of a value: 1 where it is below the median of `values` (the pixels in `used`), 0 above, -2 where unused - the rule's patches ("noise below its threshold"), half the pixels each. |
| 1375 | 13 | `static int[] stretchedField(long[] os, int side, double s, double turnDeg)` | A class field of side x side pixels at JERUS_KM from the rule's own noise (EarthTerrain.noiseAt() at a stride of one, its octaves a plot to 16 plots): its patches s to 1 at the same area, as an equal-area projection m... |
| 1393 | 15 | `static double[] response(double s, double turnDeg)` | The measure's reading of a stretch of s to 1 at the same area (stretchedField(): the equal-area form Lambert's projection gives round patches) turned `turnDeg` degrees: { the mean E of STREAK_SEEDS synthetic fields of... |
| 1410 | 1 | `static double response(double s)` | ...a stretch along x: the mean E alone. |
| 1417 | 2 | **type** `record Slant(String spot, double km, double sigma, double ePlots, double phiPlots, double eDrawn, double be...` | One placement of the city against a spot: what the plane's own plots show of the ground there (ePlots, the cause), what ground() draws with the charts (eDrawn, the fix), and the noise under the pixels - the plane's (e... |
| 1424 | 54 | `static Slant slantAt(String spot, String dataId, double[] at, double[] city)` | The city at `city`, the view at `at` at JERUS_KM, STREAK_VIEW_PX square: the streak of the plane's own plots under each pixel and of what ground() draws with the charts (every pixel on the planet drawn), the lakes' al... |
| 1479 | 137 | `static void slanted(Map<String, EarthTerrainCheck.Spot> spots, String id, EarthData full)` |  |
| 1627 | 3 | `static int shareGroup(int cls)` | The group a class counts in for the shares: water (salt, shallow, fresh), forest, grass, the rest. |
| 1632 | 75 | **type** `static final class Seam` | What a seam view reads: the class changes across the line and away from it, in the drawn ground and in its twin - the same pixels drawn by the chart alone with one noise across the line, so continuous across it - and ... |
| 1639 | 39 | `void add(String dataId, double[] at, double d, double bearing, double shiftKm, boolean abrupt)` _(in GlobeCheck.Seam)_ | One view at `at` shifted `shiftKm` along the line, the city `d` km from it on `bearing`: the line is the circle d km from the city. |
| 1680 | 1 | `double rate(int which)` _(in GlobeCheck.Seam)_ | The change rate across the line over the rate on one side, of the drawn ground (which 0) or the twin (1). |
| 1683 | 1 | `double ratio()` _(in GlobeCheck.Seam)_ | The drawn ground's ratio over the twin's: the same pixels, so the ground's own lines (a coast or river along the line) cancel. |
| 1686 | 9 | `double shareGap()` _(in GlobeCheck.Seam)_ | The most any group's share differs between the sides. |
| 1696 | 10 | `String sharesWords()` _(in GlobeCheck.Seam)_ |  |
| 1709 | 6 | `static byte[] chartNode(World.Key key, GlobeFrame.Chart chart, long plots, long nx, long ny, double fromUp, double bandRad)` | A chart node with its blend set by hand (MapTiles.chartTerrain() sets it by GlobeFrame's dials): the sphere's noise from the angle whose cosine is `fromUp`, over a band of `bandRad` - nought for none, a seam made on p... |
| 1717 | 19 | `static int[] chartOnly(GlobeFrame f, int w, int h, GlobeFrame.NodePixels nodes)` | A view's pixels all read from its chart's nodes (GlobeFrame.chartAt()), as ground() reads them where the chart draws: the plane's nodes not used. |
| 1747 | 33 | `static void seams(Map<String, EarthTerrainCheck.Spot> spots, String id, EarthData full)` | The seams (MS3): no line where the plane's ground hands over to the chart's, nor where the sphere's noise has been blended in whole - at the hand-over's circle and the band's far edge, on each fixture window and near ... |
| 1794 | 27 | `static void spread()` | The sphere's noise keeps the plane's spread (MS3): the ranking table and the frays' rules, written for noiseAt(), hold for it. |
| 1822 | 7 | `static double sd(double[] v)` |  |
| 1831 | 5 | `static double share(double[] v, double t)` | The share of `v` under t. |
| 1838 | 56 | `static void chartTiming(Map<String, EarthTerrainCheck.Spot> spots, String id, EarthData full)` | The cost of the charts (MS3): a chart node (classes, relief, colours) at Goose Bay at one plot a pixel within MapCheck.SCREEN_MS, and a frame of 2d's view over held chart nodes within FRAME_MS - the main thread's CPU,... |

### 3. THE HANDOFF (lines 1895-2011)

| line | len | member | says |
|---:|---:|---|---|
| 1900 | 14 | `static double parting(GlobeFrame f, int step)` | The most the view and the plane it hands to part, in pixels, every `step` pixels across it and its edges. |
| 1916 | 26 | `static double studyParting(GlobeFrame f, double lat, double lon, double kmPx)` | The study's measure (globe-prototype.py's reach_px): the globe north up turned by the plane's up and scaled to the plane's mean scale against the plane round the view centre, px. |
| 1943 | 53 | `static void handoff()` |  |
| 1998 | 13 | `static double northUpParting(GlobeFrame f, double lat, double lon)` | The view at (lat, lon) north up against the plane, px. |

### 4. THE PLANE'S CAMERA IS TODAY'S FAR NODES (lines 2012-2062)

| line | len | member | says |
|---:|---:|---|---|
| 2017 | 17 | `static long fingerprint(World.Key key, int from, boolean world)` | Every stride from `from` to the widest the expanded map draws, a far node aligned on the spot's and one centred on it; then, `world`, the whole world's nodes at the widest - their CRC32. |
| 2035 | 27 | `static void planeCamera(Map<String, EarthTerrainCheck.Spot> spots, String id, EarthData full)` |  |

### 5. THE GLOBE'S CAMERAS (lines 2063-2131)

| line | len | member | says |
|---:|---:|---|---|
| 2068 | 1 | `static int folded(byte b)` | A class with the forest folded into the dry ground it stands on, for comparing two dithers. |
| 2070 | 61 | `static void cameras(Map<String, EarthTerrainCheck.Spot> spots, String id, EarthData data)` |  |

### 6. THE PLANET'S PICTURE (lines 2132-2279)

| line | len | member | says |
|---:|---:|---|---|
| 2136 | 36 | `static GlobePicture picture(EarthData fixture, EarthData full)` |  |
| 2174 | 4 | `static boolean freshNear(GlobePicture p, double lat, double lon)` | Whether the picture draws fresh water within a degree of (lat, lon). |
| 2180 | 11 | **type** `static final class Land` | A picture's land per 1-degree cell against the far layers': cells with a coast, their mean and worst in points, how many past their own rounding, and the planet's land by area. |
| 2185 | 5 | `public String toString()` _(in GlobeCheck.Land)_ |  |
| 2192 | 39 | `static Land land(EarthData d, GlobePicture p)` |  |
| 2233 | 13 | **type** `static final class Rule` | The far rule at each texel, read back from the data: sea, ice and desert by the rule, the forest's share. |
| 2237 | 1 | `boolean ok()` _(in GlobeCheck.Rule)_ |  |
| 2239 | 1 | `double forestPoints()` _(in GlobeCheck.Rule)_ |  |
| 2241 | 4 | `public String toString()` _(in GlobeCheck.Rule)_ |  |
| 2247 | 32 | `static Rule rule(EarthData d, GlobePicture p)` |  |

### 7. A FRAME (lines 2280-2312)

| line | len | member | says |
|---:|---:|---|---|
| 2284 | 28 | `static void frame(GlobePicture pic)` |  |

### 8. THE RELIEF IN THE PICTURE (ET1 slice D; spec-earth2-terrain 9.3) (lines 2313-2450)

| line | len | member | says |
|---:|---:|---|---|
| 2321 | 10 | `static long[] dryTexelsByRelief(GlobePicture p, int row0, int col0, int rows, int cols)` | The dry texels (not ice, not water) in a box of the picture, `rows` x `cols` of them from (row0 from the north, col0 from 180 W), counted by relief. |
| 2333 | 17 | `static double[] dataReliefShares(EarthData d, double lat0, double lat1, double lon0, double lon1)` | The data's relief shares over a box of 30" cells (the slope layer's bytes unpacked: flat is under foothill's share, foothill what is not steep, and so on): the mean over every cell. |
| 2352 | 9 | `static boolean sharesMatch(long[] counts, double[] data)` | Whether the texels' shares (counts) are the data's within RELIEF_SIGMAS spreads and the rounding, each relief. |
| 2362 | 4 | `static String sharesWords(long[] counts)` |  |
| 2367 | 83 | `static void reliefInPicture(EarthData fixture, String id, EarthData full, GlobePicture fullPicture)` |  |

### 9 TO 12. THE NATIONS ON THE MAP (WN2, slice A; docs/studies/spec-nations.md 6.1 and 8, board 1) (lines 2451-2541)

| line | len | member | says |
|---:|---:|---|---|
| 2473 | 5 | `static GlobeFrame viewOf(double lat, double lon, double kmPx)` | A 1,389 x 868 view (MapCheck's screen) of a Halifax city looking at (lat, lon) from kmPx km a pixel; 0 for the whole planet facing the city. |
| 2480 | 12 | `static double timed(GlobeFrame f, Consumer<GlobeFrame> work)` | The least of FRAME_ROUNDS rounds, after as many to warm, of what `work` takes of the main thread's CPU, ms - each round on a view moved a little, so the one-entry cache never answers. |
| 2493 | 1 | `static String clock()` |  |
| 2495 | 46 | `static void nations() throws Exception` |  |

### 9. the levels (lines 2542-2629)

| line | len | member | says |
|---:|---:|---|---|
| 2545 | 4 | `static double[] vec(double lat, double lon)` | A vertex as a vector in km from the planet's centre, the harness's own (Math, not StrictMath). |
| 2551 | 9 | `static double chordKm(double[] p, double[] a, double[] b)` | The km from a point to a segment of the sphere's chord, the harness's own way: the perpendicular's length if its foot falls on the segment, else the distance to the nearer end. |
| 2561 | 41 | `static void bordersLevels(String scope, NationsMap map, WorldData file)` |  |
| 2604 | 25 | `static void agreeWithAt(String where, NationsMap map, WorldData file, GlobeFrame f, int least)` | Whether the lines wholly inside a view's canvas come out of bordersAt(level 0) at the pixels GlobeFrame.at() gives each vertex; at least `least` such lines. |

### 9. culled to the view, at its level, timed (lines 2630-2675)

| line | len | member | says |
|---:|---:|---|---|
| 2632 | 43 | `static void bordersCulled(NationsMap map)` |  |

### 10. the dots and labels (lines 2676-2758)

| line | len | member | says |
|---:|---:|---|---|
| 2678 | 80 | `static void dotsAndLabels(NationsMap map, WorldData file, Nations nations)` |  |

### 11. the hit test (lines 2759-2835)

| line | len | member | says |
|---:|---:|---|---|
| 2762 | 10 | `static List<double[]> expectedCells(byte[] expect)` | The fixture's cells the expect file states, {lat, lon, owner} at each cell's centre, in the file's order. |
| 2773 | 47 | `static void fixtureHits(NationsMap map, Nations nations, byte[] expect)` |  |
| 2821 | 14 | `static void fullHits(NationsMap map, Nations nations)` |  |

### 12. the card (lines 2836-2940)

| line | len | member | says |
|---:|---:|---|---|
| 2838 | 1 | `static String markWords(int mark)` |  |
| 2843 | 6 | `static boolean yearRowOk(String row, Nations nations, int nation, int year)` | Whether a card's "This year" row is right for a nation and year: a dash in the first year, else the growth with its sign and the prices, each to a decimal and within the rounding of the figure. |
| 2851 | 6 | `static boolean moneyRowOk(String row, Nations.Year y)` | Whether a card's "Its money" row is right: the US dollar in its words, else the money, its rate a US$ and what one of it is worth in game dollars, the two figures' product the game dollar's price within their roundings. |
| 2858 | 82 | `static void cards(String scope, NationsMap map, Nations nations, int lastYear)` |  |

### 13 AND 14. THE CITY'S TRADE ON THE MAP (WN3, slice C; docs/studies/spec-nations.md 8, board 1, and 6.4) (lines 2941-3391)

| line | len | member | says |
|---:|---:|---|---|
| 2953 | 4 | `static double angleBetween(double[] a, double[] b)` | The angle between two unit vectors, radians, from the chord (exact for two that are the same place, where an arc cosine reads 1.5e-8). |
| 2959 | 1 | `static double[] vertexOf(TradeLanes t, int vertex)` | A vertex of the lanes as a unit vector. |
| 2962 | 5 | `static double lengthOf(TradeLanes t, int edge)` | An edge's length along its vertices, radians. |
| 2969 | 5 | `static double medianOf(double[] v)` | The median of figures. |
| 2976 | 11 | `static double[][] everywhere(int n, long seed)` | n partners all over the planet, seeded, each over MIN_LANE both ways: {lat}, {lon}, {sold}, {bought}. |
| 2989 | 11 | `static double[][] europe()` | The twelve partners of EUROPE: {lat}, {lon}, {sold}, {bought}, the ten at 1,000 and 500, Poland at 100 and 100, Sweden at 200 and 100. |
| 3001 | 10 | `static void tradeRoutes()` |  |
| 3012 | 100 | `static void lanesShare()` |  |
| 3114 | 9 | `static boolean widthsRise(double ref)` | Whether widthPx() never falls as the money grows, over a thousand steps. |
| 3124 | 31 | `static void lanesCached()` |  |
| 3156 | 25 | `static void lanesCulled()` |  |
| 3182 | 44 | `static void lanesDrawn()` |  |
| 3227 | 30 | `static void lanesTimed(String scope, double[][] a)` |  |
| 3259 | 6 | `static double[] destination(double lat, double lon, double bearingDeg, double d)` | The place d radians from (lat, lon) on a bearing, degrees: {lat, lon}. |
| 3266 | 87 | `static void arrowsAtTheEdge()` |  |
| 3355 | 13 | `static void fullLanes(NationsMap map)` | The lanes of the full file's real nodes, 196 partners all over MIN_LANE, timed as the random ones are. |
| 3370 | 21 | `static void tradeCard(String scope, NationsMap map, Nations nations)` | The card's trade rows (13): with the year's trade, three more rows in the card's own words and formats; without, the five alone and no word of "coming". |


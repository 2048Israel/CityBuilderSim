# GlobeFrame.java - 1,078 lines · 91 methods · 19 constants · model

`ham/citybuildersim/GlobeFrame.java` - generated 2026-10-28 by CodeMap; line numbers are as of that run.

> The Earth's map as a globe, without the toolkit: where the view looks on the planet, how close and which way is up, how a notch of the wheel, a drag, a held key and Fit move it, which of three pictures it draws - the planet's picture, the ground through the globe, or the city's plane - and where a pixel is on the planet and on the city's plane; what the land office's map on Earth and the founding screen's globe draw by (since 0.7.135) and GlobeCheck and MapCheck 13 hold.
> 
> WHY THIS EXISTS (0.7.132, batch E8; docs/studies/spec-globe.md 3, Jerus's
> answers of 2026-10-11, "yes"). The map on Earth drew the whole planet on
> the city's plane, Lambert's equal-area projection round the spot: cut by
> the world's square, and the far side smeared round the rim (a quarter of
> the planet in the outer 13% of the disc). The founding screen's globe
> (E4) already drew it as a globe looks, but its arithmetic lived in the
> screen where no harness could hold it. This is that arithmetic in the
> model, for both screens: a view centre in latitude and longitude, a scale
> in km a pixel and a roll, orthographic (EarthProj.Ortho) while it is wide
> and the city's own plane, MapFrame's, closer than the handoff.
> 
> THE TIERS (spec-globe 3.1), by the scale:
>   - PICTURE, from the whole planet (FIT_SHARE of the view's shorter side)
>     down to PICTURE_FROM_KM, the planet's picture's texel: each pixel's
>     orthographic inverse, then its texel (GlobePicture.frame());
>   - GROUND, from there down to the handoff, HANDOFF_PLOTS a pixel: the far
>     rule through the globe's camera (EarthTerrain.globe(), groundPiece());
>   - PLANE, closer than the handoff: today's map, the city's plane, a
>     MapFrame at planeX(), planeY() and planeScale() - while the view's
>     centre is within the reach of the city; past it the GROUND tier
>     goes on closer than the handoff (MS1, below), drawing the plane's own
>     ground through the camera (MS2, below).
> At the handoff the plane and the globe facing the city part by a third of
> a pixel on the expanded map (Q6), so the swap cannot be seen.
> 
> WHICH WAY IS UP (spec-globe 3.3, Q4). Wide, the globe is north up. The
> plane's up is the city's north, which away from the city is turned from a
> place's own north (2.7 degrees 300 km east of Halifax); so over the last
> ROLL_NOTCHES notches before the handoff, and only while the view's centre
> is within the reach, the roll eases from north to the plane's up there
> (planeRollAt()). A globe looking farther away never hands off and stays
> north up.
> 
> WHERE CLOSE VIEWS GO (MS1, Jerus 2026-10-26: "you cant zoom in on areas of
> the map far away from the city, and if zoomed in there is a wall that you
> can cross"; spec-globe 3.4 Q3's "away from the city the wheel stops at the
> handoff, and on the plane a pan stops at the reach" is superseded). The
> wheel reaches leastKmPx() anywhere and a drag, a pan and a held key go
> anywhere at every scale: the reach no longer stops anything. It decides
> which picture a view closer than the handoff draws - the city's PLANE
> while its centre is within the reach (REACH_KM, or the city's own ground
> and its offers and REACH_MARGIN_KM round them where they reach farther:
> reachKm()), the GROUND tier beyond it, the far rule through the globe's
> camera at any scale (EarthTerrain.globe() takes any), as the founding
> screen draws it. tierOf(kmPx) is still the scale's tier and tier() the
> view's. The wall was the old stop: clampPan() pulled a centre back to the
> reach, 300 km from the city, at the plane's scales.
> 
> THE SAME GROUND PAST THE REACH (MS2, Jerus 2026-10-26: "look at the wierd
> resolution by just panning a few more kms": the far rule through the
> globe's camera has no plot under a pixel, so its forest is a dither and a
> small lake a disc, where the plane's are smooth patches and frayed lakes).
> Closer than the handoff the GROUND tier past the reach draws the plane's
> own far nodes through the camera at the scale's stride - 8, 4, 2 or 1
> plots a pixel, the nodes the plane draws inside the reach - wherever the
> world's square holds the ground (planeDrawnPastReach(), drawsHold(),
> planeHolds()); the far rule (groundPiece()) is left to what the square does
> not hold, the fifth of the planet farthest from the city. The legend and
> ... (37 more lines in the source)

**Uses:** [EarthProj](EarthProj.md) (53), [World](World.md) (28), [MapFrame](MapFrame.md) (6), [GlobePicture](GlobePicture.md) (3), [EarthTerrain](EarthTerrain.md) (2), [CityLand](CityLand.md) (1), [LandMarket](LandMarket.md) (1), [LandGrid](LandGrid.md) (1), [LandParcel](LandParcel.md) (1)

**Used by (10):** [EarthTerrainCheck](EarthTerrainCheck.md), [FoundingScreen](FoundingScreen.md), [GlobeCheck](GlobeCheck.md), [GlobePicture](GlobePicture.md), [LandMap](LandMap.md), [MapCheck](MapCheck.md), [MapTiles](MapTiles.md), [MapView](MapView.md), [NationsMap](NationsMap.md), [ReliefRenders](ReliefRenders.md)

## Sections

| line | section |
|---:|---|
| 228 | · the reach (spec-globe 3.4) |
| 273 | · which way is up (spec-globe 3.3) |
| 321 | · moving it |
| 514 | · a pixel |
| 550 | · the handoff to the city's plane (spec-globe 3.2) |
| 578 | · the GROUND tier's pieces |
| 612 | THE GLOBE ON SCREEN (0.7.135, batch E9; docs/studies/spec-globe.md 3.5 |
| 685 | · the charts: the near view far from the city, true in shape (MS3, Jerus 2026-10-27: Goose Bay from a Singapore city dr... |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 105 | `GlobeFrame.PICTURE` | `0, GROUND = 1, PLANE = 2` | The tiers, by the scale (spec-globe 3.1): the planet's picture, the ground through the globe, the city's plane. |
| 108 | `GlobeFrame.HANDOFF_PLOTS` | `16` | The handoff from the globe to the city's plane, in plots a pixel: 16 (spec-globe 3.2, Jerus's Q6) - 0.48 km a pixel, where the two part by a third of a pixel on the expanded map, a power of two the far nodes already u... |
| 111 | `GlobeFrame.HANDOFF_KM` | `HANDOFF_PLOTS * World.PLOT_M / 1000` | ...in km a pixel: HANDOFF_PLOTS x World.PLOT_M, 0.48. |
| 114 | `GlobeFrame.REACH_KM` | `300` | Closer than the handoff the view draws the city's plane while its centre is within this of the city, in km, and the GROUND tier beyond it - it stops no wheel and no pan since MS1 (spec-globe 3.4's Q3 stop is supersede... |
| 117 | `GlobeFrame.REACH_MARGIN_KM` | `50` | ...or within this of the city's own ground and its offers, in km, where they reach farther than REACH_KM: 50 (spec-globe 3.4's est., ★E8 - the ground the land office shows round what it sells; where the plane is drawn... |
| 120 | `GlobeFrame.ROLL_NOTCHES` | `2` | The roll eases from north up to the plane's up over this many notches of the wheel before the handoff: 2 (spec-globe 3.3's est., ★E8) - at most 2.7 degrees within REACH_KM of a city at Halifax's latitude, a degree and... |
| 123 | `GlobeFrame.ZOOM_STEP` | `MapFrame.ZOOM_STEP` | A notch of the wheel zooms by this much, at the pointer: the map's, MapFrame.ZOOM_STEP. |
| 126 | `GlobeFrame.FIT_SHARE` | `0.9` | Fit shows the whole planet across this share of the view's shorter side: 0.9, as the founding globe's widest view (spec-globe 3.1: 17.6 km a pixel on the expanded map). |
| 129 | `GlobeFrame.POLE_LAT` | `89` | The view's centre is turned no nearer a pole than this, in degrees: 89, the founding globe's (FoundingScreen's turn()) - at the pole every longitude is the centre's and north has no direction. |
| 132 | `GlobeFrame.PICTURE_FROM_KM` | `GlobePicture.texelKm(GlobePicture.ARCMIN)` | The PICTURE tier draws down to the planet's picture's texel, in km a pixel: a GlobePicture.ARCMIN texel's height on the ground, 7.41 (spec-globe 3.1). |
| 135 | `GlobeFrame.CROSSFADE_NOTCHES` | `1` | The swap crossfades over this many notches of the wheel inside the handoff: 1 (spec-globe 3.2's est., ★E9) - the globe laid over the plane from whole at the handoff to gone a notch in, hiding the third of a pixel they... |
| 138 | `GlobeFrame.ROLL_EASE_KM` | `REACH_MARGIN_KM` | Past the reach the roll eases back to north up over this many km (★E9; E8's ★3): REACH_MARGIN_KM, the margin the reach keeps round the city's ground - so a pan across the reach's edge at the plane's scales turns the v... |
| 141 | `GlobeFrame.EDGE_FROM_PX` | `4` | The city's edge is drawn through the globe's camera once the city is at least this many pixels across: 4 (spec-globe 3.7's est.) - under it the ring alone marks it. |
| 144 | `GlobeFrame.FOUNDING_W` | `560, FOUNDING_H = 270` | The founding screen's globe, in pixels: 560 x 270 (ui/FoundingScreen's right column, 0.7.121; GlobeFrame's since 0.7.135, so a harness draws what it draws). |
| 162 | `GlobeFrame.FOUNDING_HANDOFF_KM` | `halfPixelKm(FOUNDING_W, FOUNDING_H)` | The founding globe hands its view to the ground drawn round its centre at this, km a pixel (★E9, spec-globe 3.2's choice): halfPixelKm() of its canvas, 2.32 - under half a pixel at the swap, as the map's 16 plots is, ... |
| 695 | `GlobeFrame.SHAPE_HANDOVER` | `1.25` | The city's plane's shape distortion, k / h, past which the near view is drawn from the charts instead: 1.25 (MS3). |
| 698 | `GlobeFrame.SHAPE_BAND_KM` | `100` | The band past the hand-over over which the sphere's noise is blended in from the plane's, km along the ground: 100 (MS3) - the noise's widest octave is 480 m (the relief's 1.9 km), so over 100 km the pattern becomes t... |
| 701 | `GlobeFrame.CHART_DEG` | `10` | The charts' anchors' lattice, degrees of latitude between rows (and about as many of longitude on the equator): 10 (MS3) - the view's chart is the anchor nearest its centre, within 1,300 km of every pixel at the plane... |
| 1077 | `GlobeFrame.AT_THE_CENTRE` | `" at the centre"` | What the globe's scale bar adds to its length (spec-globe 3.7: "true at the view's centre only, and says so"). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 164 | `private double width, height` |  |
| 165 | `private final double cityLat, cityLon` |  |
| 166 | `private final EarthProj plane` |  |
| 167 | `private double lat, lon, kmPx, roll, reachKm` |  |
| 168 | `private EarthProj.Ortho ortho` |  |
| 667 | `private double drawsKm` | A copy's planeDrawsKm(), its view's; NaN in a view of its own. |
| 683 | `private Boolean pastReach` | A copy's planeDrawnPastReach(), its view's; null in a view of its own. |
| 944 | `final double lat, lon, kmPx, roll, reach, draws` |  |
| 946 | `final boolean wholeSquare` | The plane's nodes are drawn wherever the world's square holds the pixel (planeDrawnPastReach()) within the hand-over, so cosDraws is handoverUp(): every pixel at least that near the city. |
| 947 | `final double m, cosR, sinR, cosL0, sinL0, cosReach, cosDraws` |  |
| 948 | `final double ee, en, ea, ne, nn, na, ue, un, ua` |  |
| 950 | `final double xe, xn, xa, ye, yn, ya` | The view's chart's east and north rows (MS3): the same matrix as the city's, about the chart's anchor. |
| 998 | `private Composed composed` | The matrix for the view as it is, made when it moved; its fields final, so the bands' threads read it whole. |
| 1018 | `private final double ax, ay, az, bx, by, bz, cx, cy, cz, w2, h2` |  |
| 1019 | `private final int w, h` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 102 | 977 | **type** `public final class GlobeFrame` | The Earth's map as a globe, without the toolkit: where the view looks on the planet, how close and which way is up, how a notch of the wheel, a drag, a held key and Fit move it, which of three pictures it draws - the ... |
| 156 | 4 | `public static double halfPixelKm(double w, double h)` | The scale at which a view of w x h pixels and the plane centred where it looks part by half a pixel at its corners, km a pixel (spec-globe 3.2): a point rho out from the middle is the angle c = 2 asin(rho / 2R) from t... |
| 171 | 8 | `public GlobeFrame(double width, double height, double cityLat, double cityLon)` | A view of width x height pixels round a city on the real Earth at (cityLat, cityLon), the spot its plane is centred on: fitted, the whole planet facing the city, north up. |
| 180 | 1 | `public double width()` |  |
| 181 | 1 | `public double height()` |  |
| 183 | 1 | `public double centreLat()` | Where it looks, degrees. |
| 184 | 1 | `public double centreLon()` |  |
| 186 | 1 | `public double kmPx()` | How close: km a pixel at the view's centre. |
| 188 | 1 | `public double roll()` | Which way is up: the angle, degrees clockwise, from the view's up to north at its centre (0, north up, wide). |
| 190 | 1 | `public double cityLat()` | The city's spot, degrees: its plane's centre. |
| 191 | 1 | `public double cityLon()` |  |
| 193 | 1 | `public EarthProj plane()` | The city's plane (Lambert's, round the spot). |
| 196 | 1 | `public double mostKmPx()` | The widest view: the whole planet across FIT_SHARE of the shorter side, km a pixel. |
| 199 | 1 | `public static double leastKmPx()` | The closest: the plane's, MapFrame.MOST_PX_A_PLOT, km a pixel. |
| 202 | 3 | `public static int tierOf(double kmPx)` | The tier a scale draws: PICTURE from PICTURE_FROM_KM, GROUND from HANDOFF_KM, PLANE closer. |
| 207 | 4 | `public static int tierOf(double kmPx, boolean withinReach)` | The tier a view draws at a scale, its centre within the city's reach or not (MS1): tierOf(), except GROUND where that says PLANE and the centre is past the reach - the plane is drawn only round the city. |
| 213 | 4 | `public int tier()` | The tier this view draws: its scale's (tierOf()), the GROUND tier in place of the PLANE where its centre is past the reach (MS1). |
| 223 | 1 | `public int legendTier()` | The tier the legend is drawn for (MS2): the scale's own, tierOf() - the plane's at every scale closer than the handoff, past the reach too, where the ground drawn is the plane's own (planeDrawnPastReach()) and so is w... |
| 226 | 1 | `public EarthProj.Ortho ortho()` | The view's orthographic projection: round its centre, turned by its roll. |

### the reach (spec-globe 3.4) (lines 228-272)

| line | len | member | says |
|---:|---:|---|---|
| 231 | 1 | `public double reach()` | How far from the city the view's centre is drawn on the city's plane when it is closer than the handoff, km (it stops nothing since MS1). |
| 234 | 4 | `public void setReach(double km)` | The reach set (reachKm()): at least REACH_KM. |
| 240 | 9 | `public static double reachKm(CityLand land, LandMarket market)` | The reach a city's map gives close views, km (★E8, spec-globe 3.4): REACH_KM, or the farthest corner of its own ground and of its offers standing from its spot, along the ground, and REACH_MARGIN_KM - whichever is far... |
| 251 | 5 | `public static double reachKm(double farthestPlots)` | ...from the farthest of them, in plots from the spot on its plane: REACH_KM, or that along the ground and REACH_MARGIN_KM if farther. |
| 258 | 4 | `private static double corner(double x0, double y0, double x1, double y1, double c)` | The farthest corner of a box of plots from plot (c, c), in plots. |
| 264 | 3 | `public double cityKm(double atLat, double atLon)` | How far a point is from the city, km along the ground. |
| 269 | 3 | `public boolean withinReach(double atLat, double atLon)` | Whether a point is within the reach. |

### which way is up (spec-globe 3.3) (lines 273-320)

| line | len | member | says |
|---:|---:|---|---|
| 281 | 13 | `public double planeRollAt(double atLat, double atLon)` | The plane's up at a place against its north: the angle, degrees clockwise, from the plane's up to north there - what a north-up globe looking there is turned from the plane by. |
| 296 | 4 | `public static double ease(double kmPx)` | How far the roll has eased to the plane's up at a scale: 0 wider than ROLL_NOTCHES notches before the handoff, 1 at it and closer, by notches between. |
| 302 | 8 | `public double rollFor(double atLat, double atLon, double atKmPx)` | The roll a view centred at (atLat, atLon) at a scale takes: the eased share of the plane's up there within the reach, north up beyond it - since 0.7.135 (★E9) back to north up over ROLL_EASE_KM past the reach rather t... |
| 312 | 5 | `public static double crossfade(double kmPx)` | The globe's share of the picture at a scale, over the plane's: 1 at the handoff and wider, 0 CROSSFADE_NOTCHES notches inside it and closer, by notches between (spec-globe 3.2's crossfade; within the reach, where the ... |
| 319 | 1 | `public double crossfade()` | The globe's share of the picture in this view (MS1): crossfade() of its scale on the plane's tier, whole in the others - past the reach the GROUND tier is all there is, with no plane under it. |

### moving it (lines 321-513)

| line | len | member | says |
|---:|---:|---|---|
| 324 | 6 | `public void set(double atLat, double atLon, double atKmPx)` | Looks at (lat, lon) at a scale, both clamped; the roll follows. |
| 332 | 5 | `public void resize(double w, double h)` | A new size, the centre and scale kept (clamped). |
| 339 | 6 | `public void fit()` | Fit (spec-globe 3.7): the whole planet, facing the city, north up. |
| 347 | 11 | `public void zoomAt(double sx, double sy, double factor)` | Zooms about a screen point, the km a pixel times a factor (under one closer): the point on the planet under it stays under it - on the globe, on the plane, and across the handoff to within their parting - unless a cla... |
| 360 | 3 | `public void zoomNotches(double sx, double sy, double notches)` | ...by notches of the wheel, ZOOM_STEP each, in (positive) or out. |
| 365 | 10 | `public void drag(double sx0, double sy0, double sx1, double sy1)` | A drag from (sx0, sy0) to (sx1, sy1): the point grabbed stays under the pointer - the globe turns, the plane slides - at every scale and anywhere on the planet, the centre stopping at no reach (MS1: at the plane's sca... |
| 377 | 3 | `public void pan(double dx, double dy)` | A drag of (dx, dy) pixels from the centre: what a held key moves (spec-globe 3.11). |
| 382 | 4 | `public void keyPan(double[] velocity, double seconds)` | A frame of the keys held (0.7.135, spec-globe 3.11): the globe turned as a drag of MapFrame.keyStep()'s pixels from the centre would turn it, anywhere at every scale (MS1). |
| 388 | 3 | `public GlobeFrame copy(double w, double h, double atKmPx)` | This view at another size and scale - its centre and its roll as they are (the half-resolution frame a moving view is drawn at): w x h pixels at kmPx. |
| 392 | 15 | `private GlobeFrame(GlobeFrame f, double w, double h, double atKmPx)` |  |
| 409 | 4 | `public boolean sameView(GlobeFrame o)` | Whether two views draw the same picture: their size, centre, scale, roll and reach - which between them decide the tier (MS1), so two views of different tiers are never the same. |
| 419 | 29 | `private void put(double pLat, double pLon, double sx, double sy, double to)` | The view at scale `to` with (pLat, pLon) at screen point (sx, sy), in the tier it lands in (MS1): at the plane's scales on the plane by its plots, where that puts the centre within the reach; otherwise - a wider scale... |
| 460 | 18 | `static void centreFor(double pLat, double pLon, double east, double north, double nearLat, double[] out)` | The view centre from which (pLat, pLon) lies `east` and `north` metres out on the orthographic plane, north up: at the angle c = asin(rho / R) from the centre and the azimuth a = atan2(east, north) at it. |
| 480 | 6 | `private void clampZoom()` | The scale within its bounds (the wheel stops at leastKmPx() and mostKmPx(), and nowhere else since MS1); the centre off the poles; the roll. |
| 488 | 5 | `private void clampPan()` | ...for a pan: the scale and the poles' clamps, and no pull back to the reach (MS1: it was the wall - the centre stopped 300 km from the city at the plane's scales). |
| 494 | 4 | `private void update()` |  |
| 500 | 5 | `public static double bearing(double lat1, double lon1, double lat2, double lon2)` | The initial bearing from one point to another, degrees clockwise from north. |
| 507 | 6 | `public static double[] destination(double atLat, double atLon, double bearingDeg, double km)` | The point `km` from (lat, lon) along a bearing (degrees clockwise from north), on the sphere. |

### a pixel (lines 514-549)

| line | len | member | says |
|---:|---:|---|---|
| 517 | 9 | `public double[] latLonAt(double sx, double sy)` | The latitude and longitude under a screen point, in the tier this view draws - on the globe, round its centre with its roll; on the plane, its plot's; null off the planet. |
| 528 | 11 | `public double[] at(double atLat, double atLon)` | Where a latitude and longitude lie on the screen, {x, y} in pixels, in the tier this view draws: null on the globe's far side (or the plane's antipode). |
| 541 | 8 | `public boolean onPlanet(double sx, double sy)` | Whether a screen point lies on the planet as this view draws it: the globe's disc, or the plane's own (EarthProj.onPlanet()) where this view draws the plane. |

### the handoff to the city's plane (spec-globe 3.2) (lines 550-577)

| line | len | member | says |
|---:|---:|---|---|
| 553 | 4 | `public double planeX()` | The plane's view this one hands to: its centre's plot, across... |
| 559 | 4 | `public double planeY()` | ...and down... |
| 565 | 1 | `public double planeScale()` | ...and its scale, screen pixels a plot (MapFrame's): World.PLOT_M over the metres a pixel. |
| 568 | 9 | `public void fromPlane(double x, double y, double pxAPlot)` | The plane's view taken back: centred on plot (x, y) at `pxAPlot` screen pixels a plot. |

### the GROUND tier's pieces (lines 578-611)

| line | len | member | says |
|---:|---:|---|---|
| 587 | 8 | `public byte[] groundPiece(World.Key key, int sx0, int sy0, int w, int h)` | A piece of the GROUND tier: the screen's pixels sx0 to sx0 + w - 1 across and sy0 to sy0 + h - 1 down, a World class each, drawn by the far rule through the globe's camera (EarthTerrain.globe()) on the Earth world a k... |
| 602 | 9 | `public byte[] groundCodes(World.Key key, int sx0, int sy0, int w, int h)` | ...a piece's codes (ET1 slice D; spec-earth2-terrain 9.3): each pixel's class and relief as the planet's picture holds a texel's (GlobePicture.code()) - the far rule's relief through the same camera - so the GROUND ti... |

### THE GLOBE ON SCREEN (0.7.135, batch E9; docs/studies/spec-globe.md 3.5 (lines 612-684)

| line | len | member | says |
|---:|---:|---|---|
| 620 | 10 | `public boolean plotAt(double sx, double sy, double[] xy)` | The plot under a screen point in the tier this view draws, into xy: on the plane its own; on the globe through the camera to the city's plane (EarthProj.plotOf()). |
| 632 | 9 | `public double[] screenOfPlot(double x, double y)` | Where a plot of the city's plane lies on the screen, {x, y} in pixels, in the tier this view draws: null on the globe's far side or off the planet. |
| 643 | 7 | `public boolean planeHolds(double sx, double sy)` | Whether a screen point is drawn from the city's plane - within planeDrawsKm() of the city and the world's square, at the plane's scales (MS2: planeDrawnPastReach()) on the world's square within handoverKm() of the cit... |
| 662 | 3 | `public double planeDrawsKm()` | How far from the city the globe draws the city's plane's own far nodes, km, at the handoff and wider (★E9; at the plane's scales wherever the square holds the ground, planeDrawnPastReach(), MS2): the reach and the vie... |
| 677 | 1 | `public static boolean planeDrawnPastReach(double kmPx)` | Whether the city's plane's own far nodes are drawn wherever the world's square holds the ground within handoverKm() (MS3; the charts beyond) at a scale, the reach and planeDrawsKm() or not (MS2, Jerus 2026-10-26: "loo... |
| 680 | 1 | `public boolean planeDrawnPastReach()` | ...for this view: its scale's, or a copy's view's (the half-resolution frame a moving view is drawn at keeps the rule of the view it is a copy of). |

### the charts: the near view far from the city, true in shape (MS3, Jerus 2026-10-27: Goose Bay from a Singapore city dr... (lines 685-1078)

| line | len | member | says |
|---:|---:|---|---|
| 704 | 1 | `public static double distortion(double up)` | The plane's shape distortion at an angle from the city whose cosine is `up`: 2 / (1 + up) - what ground()'s k^2 / R^2 is, Snyder's k / h. |
| 707 | 1 | `public static double handoverUp()` | The cosine of the angle from the city at which the plane's shape distortion is SHAPE_HANDOVER: 2 / SHAPE_HANDOVER - 1, 0.6. |
| 710 | 1 | `public static double handoverKm()` | ...and the distance along the ground, km: 5,908. |
| 713 | 4 | **type** `public record Chart(int id, double lat, double lon)` | One chart's anchor: where its orthographic plane touches the planet (lat, lon, degrees), and the number it is kept under (a row and a column of the lattice packed: row << 8 \| column). |
| 715 | 1 | `public EarthProj.Ortho ortho()` _(in GlobeFrame.Chart)_ | The chart's projection: the globe about the anchor, north up. |
| 719 | 3 | `private static int chartsInRow(int r)` | The anchors in row `r` of the lattice (rows run from -90 in steps of CHART_DEG): as many as the row's circle is long in CHART_DEG's, at least one. |
| 729 | 19 | `public static Chart chartFor(double lat, double lon)` | The chart a point is drawn from: the nearest anchor of a lattice fixed on the planet - rows every CHART_DEG of latitude from -90 to 90, row i holding max(1, round(360 cos(lat_i) / CHART_DEG)) anchors evenly from longi... |
| 750 | 1 | `public Chart chart()` | The chart this view's ground is drawn from past the hand-over: its centre's (chartFor()). |
| 753 | 1 | **type** `public interface NodePixels` | The pixels of a far node of the city's plane, its colours (MapTiles.nodePixels()) - null where it is not drawn yet. |
| 753 | 1 | `int[] at(long nx, long ny)` _(in GlobeFrame.NodePixels)_ |  |
| 756 | 1 | **type** `public interface Beyond` | A pixel's colour beyond the reach, or where the plane's node is not drawn: by its place on the screen, and its point on the planet as a unit vector in the camera's frame (vx, vy, vz: Ortho.inverse()'s), which latOf() ... |
| 756 | 1 | `int colour(int i, int j, double vx, double vy, double vz)` _(in GlobeFrame.Beyond)_ |  |
| 759 | 3 | `public static double latOf(double vx, double vy, double vz)` | The latitude of a point given as Beyond's unit vector, degrees: Ortho.inverse()'s. |
| 764 | 3 | `public double lonOf(double vx, double vy)` | ...and its longitude, in [-180, 180). |
| 785 | 3 | `public int ground(long plots, NodePixels nodes, Beyond beyond, int space, int[] out, int outW, int i0, int i1, int j0, int j1)` | The GROUND tier near the city (★E9; spec-globe 3.5's "cheaper GROUND tier"): the screen's pixels sx in [i0, i1), sy in [j0, j1) into out (outW a row), each pixel's centre through the globe to the city's plane and, wit... |
| 796 | 67 | `public int ground(long plots, NodePixels nodes, NodePixels charts, Beyond beyond, int space, int[] out, int outW, int i0, int i...` | ...and, at the plane's scales (MS3), where the plane does not draw (past handoverKm() of the city, or off the world's square) the view's chart (chart()): `charts` gives the chart node (nx, ny)'s pixels, `plots` plots ... |
| 865 | 14 | `public boolean planeAt(double sx, double sy, double[] xy)` | The plot of the city's plane under a screen point by ground()'s matrix, into xy: false off the disc or at the plane's antipode - what a harness holds to the camera and the plane's own projections (plotAt()). |
| 881 | 7 | `public boolean reachHolds(double sx, double sy)` | Whether a screen point lies within the reach, by ground()'s matrix: the angle from the city no wider than the reach's. |
| 893 | 14 | `public boolean drawsHold(double sx, double sy)` | ...and within planeDrawsKm() - or, at the plane's scales (planeDrawnPastReach(), MS2), anywhere the world's square holds it within handoverKm() of the city (MS3: past it the charts draw): where ground() draws the city... |
| 909 | 7 | `private double cityUp(double sx, double sy)` | The cosine of the angle from the city of a screen point, by ground()'s matrix: NaN off the disc. |
| 918 | 3 | `public boolean chartHolds(double sx, double sy)` | Whether a screen point is drawn from the view's chart (MS3): on the planet at the plane's scales, and not where drawsHold() has the plane draw it - past handoverKm() of the city, or off the world's square. |
| 923 | 11 | `public boolean chartAt(double sx, double sy, double[] xy)` | A screen point's place on the view's chart, into xy, in chart pixels at the view's plots (MapFrame.nodePlotsAt(planeScale())): the anchor's orthographic east over plots x PLOT_M and minus its north over it - what grou... |
| 943 | 53 | **type** `private static final class Composed` | The view's two projections composed: the screen's metres turned by the roll into the camera's east and north (e, n) on the unit sphere, its third coordinate a = sqrt(1 - e^2 - n^2); the point's unit vector in the came... |
| 952 | 39 | `Composed(GlobeFrame f)` _(in GlobeFrame.Composed)_ |  |
| 992 | 3 | `boolean of(GlobeFrame f)` _(in GlobeFrame.Composed)_ |  |
| 1000 | 7 | `private Composed composed()` |  |
| 1017 | 41 | **type** `public static final class Carry` | Where the points of this view's pixels lie on an older view's screen (★E9): the GROUND tier is drawn again in pieces as a view comes to rest, and until a piece is in, its pixels are the last whole picture's at their o... |
| 1021 | 20 | `private Carry(GlobeFrame now, GlobeFrame old)` _(in GlobeFrame.Carry)_ |  |
| 1043 | 6 | `public boolean screen(double vx, double vy, double vz, double[] out)` _(in GlobeFrame.Carry)_ | The older view's screen point of Beyond's unit vector, into out: false on its far side. |
| 1051 | 6 | `public int pixel(double vx, double vy, double vz)` _(in GlobeFrame.Carry)_ | ...as the older view's pixel, its row times its width and its column: -1 on its far side or off its screen. |
| 1060 | 1 | `public Carry carryFrom(GlobeFrame old)` | ...this view's points on `old`'s screen. |
| 1063 | 7 | `public double[] scaleBar(double mostPx)` | The scale bar on the globe (spec-globe 3.7): MapFrame.scaleBar()'s rule at the view's centre, where alone it is true - {metres, pixels}. |
| 1072 | 3 | `public static String scaleWords(double metres)` | ...its words: the plane's, true at the centre only and saying so - "500 km at the centre". |


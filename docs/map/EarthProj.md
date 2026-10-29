# EarthProj.java - 446 lines · 34 methods · 3 constants · model

`ham/citybuildersim/EarthProj.java` - generated 2026-10-29 by CodeMap; line numbers are as of that run.

> The real Earth flattened round a city's spot: Lambert's azimuthal
> equal-area projection on a sphere, forward and inverse, the world's plots
> in metres, a tile's four corners blended, and a cell's area on the planet.
> 
> WHY. The world a city stands on has been generated from its seed since
> 0.7.56 (World); "the world as planet Earth" (the project's spec-earth.md,
> Jerus's answers of 2026-10-10: "all rec") founds a new city on a real spot
> instead, and the world's square of plots has to be laid on the round Earth
> somehow. Plate carrée was the obvious way and the wrong one: it inflates
> area by 1/cos(latitude), x1.62 at Rotterdam, and 40,075 km of equator does
> not fit the 22,610 km square. Lambert's azimuthal equal-area projection,
> centred on the spot, keeps what the books need (spec-earth 2):
>   - every km2 of the plane is a real km2 of the Earth, so the world's totals
>     are the real Earth's (a cell's area: areaOnPlanetM2(), and measured on
>     the sphere, sphericalAreaM2());
>   - true scale at the spot: at ground distance c the scale is cos(c/2)
>     along the radius and its inverse across, 0.31% off at 1,000 km, so a
>     10B city's 938 km half-diagonal stays within 0.27%;
>   - no seam at a pole or the date line. The one singular point is the
>     antipode, which maps to the circle twice the radius out (REACH_M); the
>     square's corners lie past it, and past it is off the planet (the void).
> 
> THE SPHERE is WGS 84's authalic sphere, the one with the ellipsoid's own
> area, so an equal-area map of it keeps the Earth's real km2. Everything is
> StrictMath, so a spot projects to the same plots on every machine: E2 draws
> the ground from it, and a save must find the same ground where it left it.
> 
> THE ARITHMETIC. The forward map is the textbook one (Snyder, Map
> Projections - A Working Manual, 1987, chapter 24). The inverse is written
> through the point's unit vector rather than through the textbook's
> arcsine of the latitude, which loses digits where its argument nears 1 -
> round a spot on a pole; the spec's prototype round-tripped to 2.4e-7 m
> there, and EarthCheck 1 measures this one.
> 
> THE BLEND (blend()): projecting every plot exactly costs an inverse each
> (121-128 ns, spec-earth 2); a tile of 32 x 32 plots projects its four
> corners exactly and blends latitude and longitude between them, the
> longitudes unwrapped across the date line. The blend bends from the true
> point by about side^2 x tan(latitude) / 8R - the spec measured 2.5 cm
> for a tile at Rotterdam's spot and 5.4 cm 10,000 km out - so above
> BLEND_MAX_LAT a tile projects every point exactly instead (the parallels
> curl too tightly there to blend, and at a pole the longitudes mean
> nothing).
> 
> E1 (0.7.111) built it pure; since 0.7.115 (batch E2) EarthTerrain lays
> the Earth's ground on a world with an Earth key through it - a key only
> harnesses made until 0.7.121, when the founding screen came to found a
> city on one (E4). EarthCheck holds it, EarthTerrainCheck its use.
> 
> THE GLOBE (0.7.132, batch E8; docs/studies/spec-globe.md 3.5): ortho(),
> the orthographic projection round a view centre with a roll, forward and
> inverse - the planet as GlobeFrame's wide views draw it, beside the
> Lambert plane, which stays the city's. GlobeCheck holds it.

**Uses:** [World](World.md) (12)

**Used by (18):** [EarthCheck](EarthCheck.md), [EarthDepositsCheck](EarthDepositsCheck.md), [EarthSite](EarthSite.md), [EarthTerrain](EarthTerrain.md), [EarthTerrainCheck](EarthTerrainCheck.md), [FoundingScreen](FoundingScreen.md), [Geology](Geology.md), [GlobeCheck](GlobeCheck.md), [GlobeFrame](GlobeFrame.md), [GlobePicture](GlobePicture.md), [HydroSites](HydroSites.md), [LandMap](LandMap.md), [LandmassCheck](LandmassCheck.md), [Landmasses](Landmasses.md), [MapCheck](MapCheck.md), [MapTiles](MapTiles.md), [MapView](MapView.md), [NationsMap](NationsMap.md)

## Sections

| line | section |
|---:|---|
| 97 | FORWARD AND INVERSE - degrees on the sphere, metres in the plane |
| 152 | PLOTS AND METRES - spec-earth 2: plot (x, y) is ((x - SIDE/2) x 30, (SIDE/2 - y) x 30) m |
| 181 | AREAS - a cell's on the planet, and the same measured on the sphere |
| 290 | THE BLEND - a tile's corners exact, the points between them blended |
| 350 | THE GLOBE - the orthographic view round a view centre, with a roll |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 61 | `EarthProj.RADIUS_M` | `6_371_007` | The sphere's radius in metres: WGS 84's authalic radius, the sphere with the ellipsoid's own area, so an equal-area map of it keeps the Earth's real km2 (spec-earth 2). |
| 64 | `EarthProj.REACH_M` | `2 * RADIUS_M` | How far out in the plane the planet reaches, in metres: the antipode maps to the circle of twice the radius, and past it the world's square is off the planet. |
| 67 | `EarthProj.BLEND_MAX_LAT` | `75` | The latitude, in degrees, above which a tile projects every point exactly instead of blending its corners: a blend bends from the truth by about side^2 x tan(lat) / 8R, 6.8 cm for a 960 m tile at 75 degrees, inside Ea... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 69 | `private final double lat0, lon0` | the spot, degrees |
| 70 | `private final double sinLat0, cosLat0` | the spot, degrees |
| 307 | `private final EarthProj proj` |  |
| 308 | `private final double x0, y0, side` |  |
| 309 | `private final double lat00, lat10, lat01, lat11, lon00, lon10, lon01, lon11` |  |
| 310 | `private final boolean exact` |  |
| 376 | `private final double lat0, lon0, roll` |  |
| 377 | `private final double sinLat0, cosLat0, sinRoll, cosRoll` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 58 | 389 | **type** `public final class EarthProj` | The real Earth flattened round a city's spot: Lambert's azimuthal equal-area projection on a sphere, forward and inverse, the world's plots in metres, a tile's four corners blended, and a cell's area on the planet. |
| 73 | 8 | `public EarthProj(double lat, double lon)` | The projection centred on a spot, in degrees; the longitude is taken modulo 360. |
| 83 | 1 | `public double centreLat()` | The spot's latitude, degrees. |
| 86 | 1 | `public double centreLon()` | The spot's longitude, degrees, in [-180, 180). |
| 89 | 7 | `public static double normaliseLon(double lon)` | A longitude in degrees brought into [-180, 180). |

### FORWARD AND INVERSE - degrees on the sphere, metres in the plane (lines 97-151)

| line | len | member | says |
|---:|---:|---|---|
| 105 | 19 | `public boolean forward(double lat, double lon, double[] xy)` | The plane point of (lat, lon): metres east (xy[0]) and north (xy[1]) of the spot. |
| 129 | 17 | `public boolean inverse(double x, double y, double[] latLon)` | The (lat, lon) of a plane point, degrees, the longitude in [-180, 180). |
| 148 | 3 | `public static boolean onPlanet(double x, double y)` | Whether a plane point lies on the planet: within REACH_M of the spot. |

### PLOTS AND METRES - spec-earth 2: plot (x, y) is ((x - SIDE/2) x 30, (SIDE/2 - y) x 30) m (lines 152-180)

| line | len | member | says |
|---:|---:|---|---|
| 157 | 1 | `public static double metresX(double plotX)` | Metres east of the spot of the world's plot column x (plot coordinates, so x + 0.5 is a plot's centre). |
| 160 | 1 | `public static double metresY(double plotY)` | Metres north of the spot of the world's plot row y (rows run south, so north is minus). |
| 163 | 1 | `public static double plotX(double metresX)` | The plot column of a point metres east of the spot. |
| 166 | 1 | `public static double plotY(double metresY)` | The plot row of a point metres north of the spot. |
| 169 | 3 | `public boolean latLonOfPlot(double plotX, double plotY, double[] latLon)` | The (lat, lon) of a point in plot coordinates; false off the planet. |
| 174 | 6 | `public boolean plotOf(double lat, double lon, double[] plot)` | The plot coordinates of (lat, lon); false at the antipode. |

### AREAS - a cell's on the planet, and the same measured on the sphere (lines 181-289)

| line | len | member | says |
|---:|---:|---|---|
| 191 | 9 | `public static double areaOnPlanetM2(double x0, double y0, double x1, double y1)` | The planet's area under a plane rectangle, m2: the rectangle's part within REACH_M, which, the projection being equal-area, is exactly the area on the sphere it maps to. |
| 202 | 12 | `private static double quadrant(double x, double y)` | The signed area of the disc of radius REACH_M between the axes and the corner (x, y). |
| 216 | 4 | `private static double chordIntegral(double t)` | The integral of sqrt(R'^2 - t^2) from 0 to t, R' = REACH_M: the area under the circle. |
| 222 | 5 | `public static double cellAreaKm2(int cellX, int cellY)` | The planet's area under one of the world's cells (World.CELL plots a side), km2: World.CELL_KM2 on the planet, less past the antipode's circle, 0 beyond it. |
| 236 | 21 | `public double sphericalAreaM2(double x0, double y0, double x1, double y1, int perSide)` | The area on the sphere, m2, of what a plane rectangle maps to, measured there and not in the plane: perSide points along each edge are carried to the sphere by inverse() and joined by great circles, and the polygon is... |
| 259 | 5 | `private static double[] unit(double lat, double lon)` | A point's unit vector. |
| 271 | 9 | `private static double excess(double[] o, double[] a, double[] b)` | The signed spherical excess of the triangle (o, a, b), steradians: 2 atan2(o.(a x b), 1 + o.a + a.b + b.o) (Van Oosterom and Strackee, 1983), the triple product taken on a - o and b - o, which is the same number witho... |
| 282 | 7 | `public static double distanceM(double lat1, double lon1, double lat2, double lon2)` | The great-circle distance between two points on the sphere, metres (haversine, which holds its digits for the centimetres EarthCheck measures). |

### THE BLEND - a tile's corners exact, the points between them blended (lines 290-349)

| line | len | member | says |
|---:|---:|---|---|
| 301 | 3 | `public Blend blend(double plotX, double plotY, double side)` | A square of the world's plots - corner (plotX, plotY), side plots a side, World.TILE for a tile - with its four corners projected exactly and every point between blended (spec-earth 2: four inverses a tile, bilinear b... |
| 306 | 43 | **type** `public static final class Blend` | One square's blend: see blend(). |
| 312 | 16 | `private Blend(EarthProj proj, double x0, double y0, double side)` _(in EarthProj.Blend)_ |  |
| 330 | 5 | `private static double unwrap(double lon, double ref)` _(in EarthProj.Blend)_ | A longitude moved by whole turns to within half a turn of a reference: the corners of a tile on the date line, made continuous. |
| 337 | 1 | `public boolean exact()` _(in EarthProj.Blend)_ | Whether this square projects every point exactly: off the planet in part, or above BLEND_MAX_LAT. |
| 340 | 8 | `public boolean latLon(double plotX, double plotY, double[] latLon)` _(in EarthProj.Blend)_ | The (lat, lon) of a point in plot coordinates, blended from the corners (exact if exact()); false off the planet. |

### THE GLOBE - the orthographic view round a view centre, with a roll (lines 350-446)

| line | len | member | says |
|---:|---:|---|---|
| 370 | 3 | `public static Ortho ortho(double lat, double lon, double rollDeg)` | The planet as a globe looks from space, round a view centre and turned by a roll: the orthographic projection (Snyder, Map Projections - A Working Manual, 1987, chapter 20, as the study's globe-prototype.py and the fo... |
| 375 | 71 | **type** `public static final class Ortho` | One view of the globe: see ortho(). |
| 379 | 11 | `private Ortho(double lat, double lon, double rollDeg)` _(in EarthProj.Ortho)_ |  |
| 392 | 1 | `public double centreLat()` _(in EarthProj.Ortho)_ | The view's centre, degrees. |
| 393 | 1 | `public double centreLon()` _(in EarthProj.Ortho)_ |  |
| 396 | 1 | `public double roll()` _(in EarthProj.Ortho)_ | The roll, degrees clockwise from the view's up to north at the centre. |
| 399 | 3 | `public static boolean onDisc(double x, double y)` _(in EarthProj.Ortho)_ | Whether a point of the view's plane lies on the planet's disc: within RADIUS_M of the centre. |
| 409 | 17 | `public boolean forward(double lat, double lon, double[] xy)` _(in EarthProj.Ortho)_ | The view's plane point of (lat, lon), metres right (xy[0]) and up (xy[1]) of the centre. |
| 428 | 17 | `public boolean inverse(double x, double y, double[] latLon)` _(in EarthProj.Ortho)_ | The (lat, lon) of a point of the view's plane, degrees, the longitude in [-180, 180). |


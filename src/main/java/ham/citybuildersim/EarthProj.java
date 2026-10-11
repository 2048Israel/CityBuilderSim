package ham.citybuildersim;

/**
 * The real Earth flattened round a city's spot: Lambert's azimuthal
 * equal-area projection on a sphere, forward and inverse, the world's plots
 * in metres, a tile's four corners blended, and a cell's area on the planet.
 *
 * WHY. The world a city stands on has been generated from its seed since
 * 0.7.56 (World); "the world as planet Earth" (the project's spec-earth.md,
 * Jerus's answers of 2026-10-10: "all rec") founds a new city on a real spot
 * instead, and the world's square of plots has to be laid on the round Earth
 * somehow. Plate carrée was the obvious way and the wrong one: it inflates
 * area by 1/cos(latitude), x1.62 at Rotterdam, and 40,075 km of equator does
 * not fit the 22,610 km square. Lambert's azimuthal equal-area projection,
 * centred on the spot, keeps what the books need (spec-earth 2):
 *   - every km2 of the plane is a real km2 of the Earth, so the world's totals
 *     are the real Earth's (a cell's area: areaOnPlanetM2(), and measured on
 *     the sphere, sphericalAreaM2());
 *   - true scale at the spot: at ground distance c the scale is cos(c/2)
 *     along the radius and its inverse across, 0.31% off at 1,000 km, so a
 *     10B city's 938 km half-diagonal stays within 0.27%;
 *   - no seam at a pole or the date line. The one singular point is the
 *     antipode, which maps to the circle twice the radius out (REACH_M); the
 *     square's corners lie past it, and past it is off the planet (the void).
 *
 * THE SPHERE is WGS 84's authalic sphere, the one with the ellipsoid's own
 * area, so an equal-area map of it keeps the Earth's real km2. Everything is
 * StrictMath, so a spot projects to the same plots on every machine: E2 draws
 * the ground from it, and a save must find the same ground where it left it.
 *
 * THE ARITHMETIC. The forward map is the textbook one (Snyder, Map
 * Projections - A Working Manual, 1987, chapter 24). The inverse is written
 * through the point's unit vector rather than through the textbook's
 * arcsine of the latitude, which loses digits where its argument nears 1 -
 * round a spot on a pole; the spec's prototype round-tripped to 2.4e-7 m
 * there, and EarthCheck 1 measures this one.
 *
 * THE BLEND (blend()): projecting every plot exactly costs an inverse each
 * (121-128 ns, spec-earth 2); a tile of 32 x 32 plots projects its four
 * corners exactly and blends latitude and longitude between them, the
 * longitudes unwrapped across the date line. The blend bends from the true
 * point by about side^2 x tan(latitude) / 8R - the spec measured 2.5 cm
 * for a tile at Rotterdam's spot and 5.4 cm 10,000 km out - so above
 * BLEND_MAX_LAT a tile projects every point exactly instead (the parallels
 * curl too tightly there to blend, and at a pole the longitudes mean
 * nothing).
 *
 * E1 (0.7.111) built it pure; since 0.7.115 (batch E2) EarthTerrain lays
 * the Earth's ground on a world with an Earth key through it - a key only
 * harnesses make until E4. EarthCheck holds it, EarthTerrainCheck its use.
 */
public final class EarthProj {

    /** The sphere's radius in metres: WGS 84's authalic radius, the sphere with the ellipsoid's own area, so an equal-area map of it keeps the Earth's real km2 (spec-earth 2). */
    public static final double RADIUS_M = 6_371_007;

    /** How far out in the plane the planet reaches, in metres: the antipode maps to the circle of twice the radius, and past it the world's square is off the planet. */
    public static final double REACH_M = 2 * RADIUS_M;

    /** The latitude, in degrees, above which a tile projects every point exactly instead of blending its corners: a blend bends from the truth by about side^2 x tan(lat) / 8R, 6.8 cm for a 960 m tile at 75 degrees, inside EarthCheck's 0.1 m, and 10.3 cm at 80. */
    public static final double BLEND_MAX_LAT = 75;

    private final double lat0, lon0;            // the spot, degrees
    private final double sinLat0, cosLat0;

    /** The projection centred on a spot, in degrees; the longitude is taken modulo 360. */
    public EarthProj(double lat, double lon) {
        if (!(lat >= -90 && lat <= 90)) throw new IllegalArgumentException("latitude " + lat);
        this.lat0 = lat;
        this.lon0 = normaliseLon(lon);
        double p = StrictMath.toRadians(lat);
        this.sinLat0 = StrictMath.sin(p);
        this.cosLat0 = StrictMath.cos(p);
    }

    /** The spot's latitude, degrees. */
    public double centreLat() { return lat0; }

    /** The spot's longitude, degrees, in [-180, 180). */
    public double centreLon() { return lon0; }

    /** A longitude in degrees brought into [-180, 180). */
    public static double normaliseLon(double lon) {
        if (lon >= -180 && lon < 180) return lon;                  // untouched: a turn added and taken away would round it
        double t = (lon + 180) % 360;
        if (t < 0) t += 360;
        if (t >= 360) t -= 360;
        return t - 180;
    }

    /* =====================================================================
       FORWARD AND INVERSE - degrees on the sphere, metres in the plane
       ===================================================================== */

    /**
     * The plane point of (lat, lon): metres east (xy[0]) and north (xy[1]) of
     * the spot. False at the antipode, the one point every direction reaches.
     */
    public boolean forward(double lat, double lon, double[] xy) {
        double p = StrictMath.toRadians(lat);
        double sinP = StrictMath.sin(p), cosP = StrictMath.cos(p);
        double dl = StrictMath.toRadians(normaliseLon(lon - lon0));
        double cosDl = StrictMath.cos(dl), sinDl = StrictMath.sin(dl);
        double up = sinLat0 * sinP + cosLat0 * cosP * cosDl;          // cos of the angle from the spot
        double east = cosP * sinDl;
        double north = cosLat0 * sinP - sinLat0 * cosP * cosDl;
        double one = 1 + up;
        if (!(one > 0)) {
            xy[0] = Double.NaN;
            xy[1] = Double.NaN;
            return false;
        }
        double k = RADIUS_M * StrictMath.sqrt(2 / one);
        xy[0] = k * east;
        xy[1] = k * north;
        return true;
    }

    /**
     * The (lat, lon) of a plane point, degrees, the longitude in [-180, 180).
     * False past REACH_M, where the plane is off the planet.
     */
    public boolean inverse(double x, double y, double[] latLon) {
        double s2 = (x * x + y * y) / (REACH_M * REACH_M);         // sin^2 of half the angle from the spot
        if (s2 > 1) {
            latLon[0] = Double.NaN;
            latLon[1] = Double.NaN;
            return false;
        }
        double a = 1 - 2 * s2;                                      // cos of the angle
        double b = StrictMath.sqrt(1 - s2) / RADIUS_M;              // sin of the angle, over the plane distance
        // the point's unit vector in the frame whose x axis is the spot's meridian at the equator
        double px = a * cosLat0 - b * y * sinLat0;
        double py = b * x;
        double pz = a * sinLat0 + b * y * cosLat0;
        latLon[0] = StrictMath.toDegrees(StrictMath.atan2(pz, StrictMath.hypot(px, py)));
        latLon[1] = normaliseLon(lon0 + StrictMath.toDegrees(StrictMath.atan2(py, px)));
        return true;
    }

    /** Whether a plane point lies on the planet: within REACH_M of the spot. */
    public static boolean onPlanet(double x, double y) {
        return x * x + y * y <= REACH_M * REACH_M;
    }

    /* =====================================================================
       PLOTS AND METRES - spec-earth 2: plot (x, y) is ((x - SIDE/2) x 30, (SIDE/2 - y) x 30) m
       ===================================================================== */

    /** Metres east of the spot of the world's plot column x (plot coordinates, so x + 0.5 is a plot's centre). */
    public static double metresX(double plotX) { return (plotX - World.SIDE / 2.0) * World.PLOT_M; }

    /** Metres north of the spot of the world's plot row y (rows run south, so north is minus). */
    public static double metresY(double plotY) { return (World.SIDE / 2.0 - plotY) * World.PLOT_M; }

    /** The plot column of a point metres east of the spot. */
    public static double plotX(double metresX) { return metresX / World.PLOT_M + World.SIDE / 2.0; }

    /** The plot row of a point metres north of the spot. */
    public static double plotY(double metresY) { return World.SIDE / 2.0 - metresY / World.PLOT_M; }

    /** The (lat, lon) of a point in plot coordinates; false off the planet. */
    public boolean latLonOfPlot(double plotX, double plotY, double[] latLon) {
        return inverse(metresX(plotX), metresY(plotY), latLon);
    }

    /** The plot coordinates of (lat, lon); false at the antipode. */
    public boolean plotOf(double lat, double lon, double[] plot) {
        if (!forward(lat, lon, plot)) return false;
        plot[0] = plotX(plot[0]);
        plot[1] = plotY(plot[1]);
        return true;
    }

    /* =====================================================================
       AREAS - a cell's on the planet, and the same measured on the sphere
       ===================================================================== */

    /**
     * The planet's area under a plane rectangle, m2: the rectangle's part
     * within REACH_M, which, the projection being equal-area, is exactly the
     * area on the sphere it maps to. Closed form; it does not depend on the
     * spot.
     */
    public static double areaOnPlanetM2(double x0, double y0, double x1, double y1) {
        double xa = Math.min(x0, x1), xb = Math.max(x0, x1), ya = Math.min(y0, y1), yb = Math.max(y0, y1);
        double nx = xa > 0 ? xa : xb < 0 ? -xb : 0, ny = ya > 0 ? ya : yb < 0 ? -yb : 0;
        double fx = Math.max(-xa, xb), fy = Math.max(-ya, yb);
        double r2 = REACH_M * REACH_M;
        if (nx * nx + ny * ny >= r2) return 0;                     // wholly past the reach
        if (fx * fx + fy * fy <= r2) return (xb - xa) * (yb - ya);  // wholly on the planet
        return quadrant(xb, yb) - quadrant(xa, yb) - quadrant(xb, ya) + quadrant(xa, ya);
    }

    /** The signed area of the disc of radius REACH_M between the axes and the corner (x, y). */
    private static double quadrant(double x, double y) {
        double r = REACH_M;
        double ax = Math.min(Math.abs(x), r), ay = Math.min(Math.abs(y), r);
        double f;
        if (ax == 0 || ay == 0) f = 0;
        else if (ax * ax + ay * ay <= r * r) f = ax * ay;
        else {
            double u = StrictMath.sqrt(r * r - ay * ay);            // where the circle meets the row y
            f = u * ay + chordIntegral(ax) - chordIntegral(u);
        }
        return Math.signum(x) * Math.signum(y) * f;
    }

    /** The integral of sqrt(R'^2 - t^2) from 0 to t, R' = REACH_M: the area under the circle. */
    private static double chordIntegral(double t) {
        double r = REACH_M;
        return (t * StrictMath.sqrt(Math.max(0, r * r - t * t)) + r * r * StrictMath.asin(Math.min(1, t / r))) / 2;
    }

    /** The planet's area under one of the world's cells (World.CELL plots a side), km2: World.CELL_KM2 on the planet, less past the antipode's circle, 0 beyond it. */
    public static double cellAreaKm2(int cellX, int cellY) {
        double x0 = metresX((double) cellX * World.CELL), x1 = metresX((double) (cellX + 1) * World.CELL);
        double y0 = metresY((double) cellY * World.CELL), y1 = metresY((double) (cellY + 1) * World.CELL);
        return areaOnPlanetM2(x0, y0, x1, y1) / 1e6;
    }

    /**
     * The area on the sphere, m2, of what a plane rectangle maps to, measured
     * there and not in the plane: perSide points along each edge are carried
     * to the sphere by inverse() and joined by great circles, and the polygon
     * is summed as triangles from the image of the rectangle's centre, each
     * by its spherical excess. Only meaningful for a rectangle on the planet;
     * it is the independent measure EarthCheck holds the equal-area claim to.
     */
    public double sphericalAreaM2(double x0, double y0, double x1, double y1, int perSide) {
        double[] ll = new double[2];
        if (!inverse((x0 + x1) / 2, (y0 + y1) / 2, ll)) return Double.NaN;
        double[] o = unit(ll[0], ll[1]);
        double[] xs = { x0, x1, x1, x0 }, ys = { y0, y0, y1, y1 };  // round the rectangle, counter-clockwise from above
        double sum = 0;
        double[] prev = null, first = null;
        for (int side = 0; side < 4; side++) {
            double ax = xs[side], ay = ys[side], bx = xs[(side + 1) % 4], by = ys[(side + 1) % 4];
            for (int i = 0; i < perSide; i++) {
                double t = (double) i / perSide;
                if (!inverse(ax + (bx - ax) * t, ay + (by - ay) * t, ll)) return Double.NaN;
                double[] p = unit(ll[0], ll[1]);
                if (prev != null) sum += excess(o, prev, p);
                else first = p;
                prev = p;
            }
        }
        sum += excess(o, prev, first);
        return Math.abs(sum) * RADIUS_M * RADIUS_M;
    }

    /** A point's unit vector. */
    private static double[] unit(double lat, double lon) {
        double p = StrictMath.toRadians(lat), l = StrictMath.toRadians(lon);
        double c = StrictMath.cos(p);
        return new double[] { c * StrictMath.cos(l), c * StrictMath.sin(l), StrictMath.sin(p) };
    }

    /**
     * The signed spherical excess of the triangle (o, a, b), steradians:
     * 2 atan2(o.(a x b), 1 + o.a + a.b + b.o) (Van Oosterom and Strackee,
     * 1983), the triple product taken on a - o and b - o, which is the same
     * number without the cancellation of nearly equal vectors.
     */
    private static double excess(double[] o, double[] a, double[] b) {
        double ax = a[0] - o[0], ay = a[1] - o[1], az = a[2] - o[2];
        double bx = b[0] - o[0], by = b[1] - o[1], bz = b[2] - o[2];
        double triple = o[0] * (ay * bz - az * by) + o[1] * (az * bx - ax * bz) + o[2] * (ax * by - ay * bx);
        double oa = o[0] * a[0] + o[1] * a[1] + o[2] * a[2];
        double ab = a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
        double bo = b[0] * o[0] + b[1] * o[1] + b[2] * o[2];
        return 2 * StrictMath.atan2(triple, 1 + oa + ab + bo);
    }

    /** The great-circle distance between two points on the sphere, metres (haversine, which holds its digits for the centimetres EarthCheck measures). */
    public static double distanceM(double lat1, double lon1, double lat2, double lon2) {
        double dp = StrictMath.toRadians(lat2 - lat1);
        double dl = StrictMath.toRadians(normaliseLon(lon2 - lon1));
        double s = StrictMath.sin(dp / 2), t = StrictMath.sin(dl / 2);
        double h = s * s + StrictMath.cos(StrictMath.toRadians(lat1)) * StrictMath.cos(StrictMath.toRadians(lat2)) * t * t;
        return 2 * RADIUS_M * StrictMath.asin(Math.min(1, StrictMath.sqrt(h)));
    }

    /* =====================================================================
       THE BLEND - a tile's corners exact, the points between them blended
       ===================================================================== */

    /**
     * A square of the world's plots - corner (plotX, plotY), side plots a
     * side, World.TILE for a tile - with its four corners projected exactly
     * and every point between blended (spec-earth 2: four inverses a tile,
     * bilinear between them). Two tiles that share an edge blend it from the
     * same two corners, so they agree along it.
     */
    public Blend blend(double plotX, double plotY, double side) {
        return new Blend(this, plotX, plotY, side);
    }

    /** One square's blend: see blend(). */
    public static final class Blend {
        private final EarthProj proj;
        private final double x0, y0, side;
        private final double lat00, lat10, lat01, lat11, lon00, lon10, lon01, lon11;
        private final boolean exact;

        private Blend(EarthProj proj, double x0, double y0, double side) {
            this.proj = proj;
            this.x0 = x0;
            this.y0 = y0;
            this.side = side;
            double[] c00 = new double[2], c10 = new double[2], c01 = new double[2], c11 = new double[2];
            boolean on = proj.latLonOfPlot(x0, y0, c00) & proj.latLonOfPlot(x0 + side, y0, c10)
                    & proj.latLonOfPlot(x0, y0 + side, c01) & proj.latLonOfPlot(x0 + side, y0 + side, c11);
            boolean polar = Math.max(Math.max(Math.abs(c00[0]), Math.abs(c10[0])), Math.max(Math.abs(c01[0]), Math.abs(c11[0]))) > BLEND_MAX_LAT;
            this.exact = !on || polar;
            lat00 = c00[0]; lat10 = c10[0]; lat01 = c01[0]; lat11 = c11[0];
            lon00 = c00[1];
            lon10 = unwrap(c10[1], lon00);
            lon01 = unwrap(c01[1], lon00);
            lon11 = unwrap(c11[1], lon00);
        }

        /** A longitude moved by whole turns to within half a turn of a reference: the corners of a tile on the date line, made continuous. */
        private static double unwrap(double lon, double ref) {
            if (lon - ref > 180) return lon - 360;
            if (lon - ref < -180) return lon + 360;
            return lon;
        }

        /** Whether this square projects every point exactly: off the planet in part, or above BLEND_MAX_LAT. */
        public boolean exact() { return exact; }

        /** The (lat, lon) of a point in plot coordinates, blended from the corners (exact if exact()); false off the planet. */
        public boolean latLon(double plotX, double plotY, double[] latLon) {
            if (exact) return proj.latLonOfPlot(plotX, plotY, latLon);
            double u = (plotX - x0) / side, v = (plotY - y0) / side;
            double iu = 1 - u, iv = 1 - v;
            latLon[0] = (lat00 * iu + lat10 * u) * iv + (lat01 * iu + lat11 * u) * v;
            latLon[1] = normaliseLon((lon00 * iu + lon10 * u) * iv + (lon01 * iu + lon11 * u) * v);
            return true;
        }
    }
}
